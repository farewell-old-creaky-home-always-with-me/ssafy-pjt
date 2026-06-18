package com.ssafy.home.external.vworld;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

public class VworldLegalRegionClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final VworldProperties properties;

    public VworldLegalRegionClient(
            RestClient restClient,
            ObjectMapper objectMapper,
            VworldProperties properties
    ) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public VworldRegionPage fetch(String sidoCode, int pageNumber) {
        try {
            String response = restClient.get()
                    .uri(requestUri(sidoCode, pageNumber))
                    .retrieve()
                    .body(String.class);
            return parse(response);
        } catch (RestClientResponseException exception) {
            boolean retryable = exception.getStatusCode().is5xxServerError()
                    || exception.getStatusCode().value() == 429;
            throw new VworldApiException("Failed to call VWorld API", exception, retryable);
        } catch (ResourceAccessException exception) {
            throw new VworldApiException("Failed to call VWorld API", exception, true);
        } catch (RestClientException exception) {
            throw new VworldApiException("Failed to call VWorld API", exception, false);
        }
    }

    private URI requestUri(String sidoCode, int pageNumber) {
        return UriComponentsBuilder.fromUri(properties.dataUrl())
                .queryParam("service", "data")
                .queryParam("request", "GetFeature")
                .queryParam("version", "2.0")
                .queryParam("key", properties.apiKey())
                .queryParam("domain", properties.domain())
                .queryParam("format", "json")
                .queryParam("size", properties.pageSize())
                .queryParam("page", pageNumber)
                .queryParam("data", properties.legalEmdLayer())
                .queryParam("geometry", "false")
                .queryParam("attribute", "true")
                .queryParam("attrFilter", "bjcd:like:" + sidoCode + "*")
                .build(true)
                .toUri();
    }

    private VworldRegionPage parse(String source) {
        if (source == null || source.isBlank()) {
            throw new VworldApiException("Blank VWorld response", null, true);
        }
        try {
            JsonNode response = objectMapper.readTree(source).path("response");
            String status = text(response.path("status"));
            if (status != null && !"OK".equalsIgnoreCase(status)) {
                String message = text(response.path("error").path("text"));
                throw new VworldApiException(
                        message == null ? "VWorld API error: " + status : message
                );
            }
            JsonNode features = response.path("result")
                    .path("featureCollection")
                    .path("features");
            List<VworldRawRegion> regions = new ArrayList<>();
            if (features.isArray()) {
                features.forEach(node -> regions.add(toRaw(node.path("properties"))));
            }
            int totalCount = integer(response.path("record").path("total"));
            return new VworldRegionPage(regions, totalCount);
        } catch (JsonProcessingException exception) {
            throw new VworldApiException("Failed to parse VWorld response", exception, true);
        }
    }

    private VworldRawRegion toRaw(JsonNode properties) {
        String regionCode = firstNonBlank(
                text(properties.path("bjcd")),
                text(properties.path("emd_cd"))
        );
        boolean abolished = isAbolished(properties);
        return new VworldRawRegion(
                regionCode,
                text(properties.path("sido_nm")),
                text(properties.path("sgg_nm")),
                text(properties.path("emd_nm")),
                abolished
        );
    }

    private boolean isAbolished(JsonNode properties) {
        String abolishedFlag = text(properties.path("abol_en"));
        if ("Y".equalsIgnoreCase(abolishedFlag)) {
            return true;
        }
        String abolishedDate = firstNonBlank(
                text(properties.path("abol_dt")),
                text(properties.path("abol_p"))
        );
        return abolishedDate != null && !abolishedDate.isBlank();
    }

    private String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first.trim();
        }
        if (second != null && !second.isBlank()) {
            return second.trim();
        }
        return null;
    }

    private String text(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        String value = node.asText().trim();
        return value.isEmpty() ? null : value;
    }

    private int integer(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            throw new VworldApiException("Missing totalCount from VWorld API");
        }
        String text = node.asText().trim();
        if (text.isEmpty()) {
            throw new VworldApiException("Missing totalCount from VWorld API");
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            throw new VworldApiException(
                    "Unexpected totalCount format from VWorld API: '" + text + "'",
                    exception,
                    false
            );
        }
    }
}
