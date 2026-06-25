package com.ssafy.home.external.seoul.cctv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

public class SeoulCctvClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final SeoulCctvProperties properties;

    public SeoulCctvClient(
            RestClient restClient,
            ObjectMapper objectMapper,
            SeoulCctvProperties properties
    ) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public SeoulCctvPage fetch(int pageNo) {
        int start = ((pageNo - 1) * properties.pageSize()) + 1;
        int end = pageNo * properties.pageSize();
        try {
            String response = restClient.get()
                    .uri(UriComponentsBuilder.fromUri(properties.baseUrl())
                            .pathSegment(properties.serviceKey(), "json", properties.serviceName(),
                                    String.valueOf(start), String.valueOf(end))
                            .build(false)
                            .toUri())
                    .retrieve()
                    .body(String.class);
            return parse(response);
        } catch (RestClientResponseException exception) {
            boolean retryable = exception.getStatusCode().is5xxServerError()
                    || exception.getStatusCode().value() == 429;
            throw new SeoulCctvApiException("Failed to call Seoul CCTV OpenData API", exception, retryable);
        } catch (ResourceAccessException exception) {
            throw new SeoulCctvApiException("Failed to call Seoul CCTV OpenData API", exception, true);
        } catch (RestClientException exception) {
            throw new SeoulCctvApiException("Failed to call Seoul CCTV OpenData API", exception, false);
        }
    }

    private SeoulCctvPage parse(String source) {
        if (source == null || source.isBlank()) {
            throw new SeoulCctvApiException("Blank Seoul CCTV OpenData response", null, true);
        }
        try {
            JsonNode root = objectMapper.readTree(source);
            JsonNode dataRoot = root.path(properties.serviceName());
            if (dataRoot.isMissingNode() || dataRoot.isNull()) {
                throw new SeoulCctvApiException("Missing service root in Seoul CCTV OpenData response");
            }
            validateResult(dataRoot);
            int totalCount = parseTotalCount(dataRoot);

            JsonNode rowNode = dataRoot.path("row");
            if (rowNode.isMissingNode() || rowNode.isNull()) {
                return new SeoulCctvPage(List.of(), totalCount);
            }
            if (!rowNode.isArray()) {
                throw new SeoulCctvApiException("Invalid row array in Seoul CCTV OpenData response");
            }

            List<SeoulRawCctv> rows = new ArrayList<>();
            rowNode.forEach(node -> rows.add(toRaw(node)));
            return new SeoulCctvPage(rows, totalCount);
        } catch (JsonProcessingException exception) {
            throw new SeoulCctvApiException("Failed to parse Seoul CCTV OpenData response", exception, true);
        }
    }

    private void validateResult(JsonNode dataRoot) {
        JsonNode codeNode = dataRoot.path("RESULT").path("CODE");
        if (!codeNode.isMissingNode() && !codeNode.asText().startsWith("INFO-")) {
            throw new SeoulCctvApiException("Seoul CCTV OpenData API returned error: " + codeNode.asText());
        }
    }

    private int parseTotalCount(JsonNode dataRoot) {
        JsonNode totalCountNode = dataRoot.path("list_total_count");
        if (totalCountNode.isMissingNode() || totalCountNode.isNull()) {
            throw new SeoulCctvApiException("Missing list_total_count in Seoul CCTV OpenData response");
        }
        String totalCountText = totalCountNode.asText().trim();
        try {
            return Integer.parseInt(totalCountText);
        } catch (NumberFormatException e) {
            throw new SeoulCctvApiException(
                    "Invalid list_total_count in Seoul CCTV OpenData response: '" + totalCountText + "'",
                    e,
                    false
            );
        }
    }

    private SeoulRawCctv toRaw(JsonNode node) {
        return new SeoulRawCctv(
                text(node.path(properties.purposeField())),
                text(node.path(properties.cameraCountField())),
                textField(node, properties.addressField()),
                text(node.path(properties.latitudeField())),
                text(node.path(properties.longitudeField()))
        );
    }

    private String textField(JsonNode node, String field) {
        if (field == null || field.isBlank()) {
            return null;
        }
        return text(node.path(field));
    }

    private String text(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        String value = node.asText().trim();
        return value.isEmpty() ? null : value;
    }
}
