package com.ssafy.home.external.sdsc;

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

public class SdscStoreClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final SdscProperties properties;

    public SdscStoreClient(RestClient restClient, ObjectMapper objectMapper, SdscProperties properties) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public SdscStorePage fetch(String sigunguCode, int pageNo) {
        try {
            String response = restClient.get()
                    .uri(buildUri(sigunguCode, pageNo))
                    .retrieve()
                    .body(String.class);
            return parse(response);
        } catch (RestClientResponseException exception) {
            boolean retryable = exception.getStatusCode().is5xxServerError()
                    || exception.getStatusCode().value() == 429;
            throw new SdscApiException("Failed to call SDSC API", exception, retryable);
        } catch (ResourceAccessException exception) {
            throw new SdscApiException("Failed to call SDSC API", exception, true);
        } catch (RestClientException exception) {
            throw new SdscApiException("Failed to call SDSC API", exception, false);
        }
    }

    private java.net.URI buildUri(String sigunguCode, int pageNo) {
        return UriComponentsBuilder.fromUri(properties.storeListUrl())
                .queryParam("serviceKey", properties.serviceKey())
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", properties.pageSize())
                .queryParam("divId", "SIG")
                .queryParam("key", sigunguCode)
                .build(false)
                .toUri();
    }

    private SdscStorePage parse(String source) {
        if (source == null || source.isBlank()) {
            throw new SdscApiException("Blank SDSC response", null, true);
        }
        try {
            JsonNode root = objectMapper.readTree(source);
            int totalCount = root.path("totalCount").asInt(0);
            JsonNode dataNode = root.path("data");
            List<SdscRawStore> stores = new ArrayList<>();
            if (dataNode.isArray()) {
                dataNode.forEach(node -> stores.add(toRaw(node)));
            }
            return new SdscStorePage(stores, totalCount);
        } catch (JsonProcessingException exception) {
            throw new SdscApiException("Failed to parse SDSC response", exception, true);
        }
    }

    private SdscRawStore toRaw(JsonNode node) {
        return new SdscRawStore(
                text(node.path("bizesId")),
                text(node.path("bizesNm")),
                text(node.path("indsLclsNm")),
                text(node.path("indsMclsNm")),
                text(node.path("indsSclsNm")),
                text(node.path("lat")),
                text(node.path("lon")),
                text(node.path("rdnmAdr")),
                text(node.path("lnoAdr"))
        );
    }

    private String text(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        String value = node.asText().trim();
        return value.isEmpty() ? null : value;
    }
}
