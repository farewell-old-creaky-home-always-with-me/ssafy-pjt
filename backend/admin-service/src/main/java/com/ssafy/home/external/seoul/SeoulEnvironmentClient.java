package com.ssafy.home.external.seoul;

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

public class SeoulEnvironmentClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final SeoulEnvironmentProperties properties;

    public SeoulEnvironmentClient(
            RestClient restClient,
            ObjectMapper objectMapper,
            SeoulEnvironmentProperties properties
    ) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public SeoulEnvironmentPage fetch(String datasetKey, int pageNo) {
        SeoulEnvironmentProperties.Dataset dataset = dataset(datasetKey);
        int start = ((pageNo - 1) * properties.pageSize()) + 1;
        int end = pageNo * properties.pageSize();
        try {
            String response = restClient.get()
                    .uri(UriComponentsBuilder.fromUri(properties.baseUrl())
                            .pathSegment(properties.serviceKey(), "json", dataset.serviceName(),
                                    String.valueOf(start), String.valueOf(end))
                            .build(false)
                            .toUri())
                    .retrieve()
                    .body(String.class);
            return parse(dataset, response);
        } catch (RestClientResponseException exception) {
            boolean retryable = exception.getStatusCode().is5xxServerError()
                    || exception.getStatusCode().value() == 429;
            throw new SeoulEnvironmentApiException("Failed to call Seoul OpenData API", exception, retryable);
        } catch (ResourceAccessException exception) {
            throw new SeoulEnvironmentApiException("Failed to call Seoul OpenData API", exception, true);
        } catch (RestClientException exception) {
            throw new SeoulEnvironmentApiException("Failed to call Seoul OpenData API", exception, false);
        }
    }

    private SeoulEnvironmentProperties.Dataset dataset(String datasetKey) {
        return properties.datasets().stream()
                .filter(dataset -> dataset.key().equals(datasetKey))
                .findFirst()
                .orElseThrow(() -> new SeoulEnvironmentApiException("Unknown Seoul environment dataset: " + datasetKey));
    }

    private SeoulEnvironmentPage parse(SeoulEnvironmentProperties.Dataset dataset, String source) {
        if (source == null || source.isBlank()) {
            throw new SeoulEnvironmentApiException("Blank Seoul OpenData response", null, true);
        }
        try {
            JsonNode root = objectMapper.readTree(source);
            JsonNode dataRoot = root.path(dataset.serviceName());
            if (dataRoot.isMissingNode() || dataRoot.isNull()) {
                throw new SeoulEnvironmentApiException("Missing dataset root in Seoul OpenData response");
            }
            validateResult(dataRoot);
            int totalCount = parseTotalCount(dataRoot);

            JsonNode rowNode = dataRoot.path("row");
            if (rowNode.isMissingNode() || rowNode.isNull()) {
                return new SeoulEnvironmentPage(List.of(), totalCount);
            }
            if (!rowNode.isArray()) {
                throw new SeoulEnvironmentApiException("Invalid row array in Seoul OpenData response");
            }

            List<SeoulRawEnvironment> rows = new ArrayList<>();
            rowNode.forEach(node -> rows.add(toRaw(dataset, node)));
            return new SeoulEnvironmentPage(rows, totalCount);
        } catch (JsonProcessingException exception) {
            throw new SeoulEnvironmentApiException("Failed to parse Seoul OpenData response", exception, true);
        }
    }

    private void validateResult(JsonNode dataRoot) {
        JsonNode codeNode = dataRoot.path("RESULT").path("CODE");
        if (!codeNode.isMissingNode() && !codeNode.asText().startsWith("INFO-")) {
            throw new SeoulEnvironmentApiException("Seoul OpenData API returned error: " + codeNode.asText());
        }
    }

    private int parseTotalCount(JsonNode dataRoot) {
        JsonNode totalCountNode = dataRoot.path("list_total_count");
        if (totalCountNode.isMissingNode() || totalCountNode.isNull()) {
            throw new SeoulEnvironmentApiException("Missing list_total_count in Seoul OpenData response");
        }
        String totalCountText = totalCountNode.asText().trim();
        try {
            return Integer.parseInt(totalCountText);
        } catch (NumberFormatException e) {
            throw new SeoulEnvironmentApiException(
                    "Invalid list_total_count format in Seoul OpenData response: '" + totalCountText + "'",
                    e,
                    false
            );
        }
    }

    private SeoulRawEnvironment toRaw(SeoulEnvironmentProperties.Dataset dataset, JsonNode node) {
        return new SeoulRawEnvironment(
                dataset.key(),
                dataset.category(),
                text(node.path(dataset.itemNameField())),
                textField(node, dataset.valueField()),
                textField(node, dataset.unitField()),
                textField(node, dataset.measuredDateField()),
                text(node.path(dataset.latitudeField())),
                text(node.path(dataset.longitudeField()))
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
