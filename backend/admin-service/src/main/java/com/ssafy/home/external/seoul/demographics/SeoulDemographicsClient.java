package com.ssafy.home.external.seoul.demographics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

public class SeoulDemographicsClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final SeoulDemographicsProperties properties;

    public SeoulDemographicsClient(
            RestClient restClient,
            ObjectMapper objectMapper,
            SeoulDemographicsProperties properties
    ) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public SeoulDemographicsPage<SeoulRawPopulation> fetchPopulation(int pageNo) {
        SeoulDemographicsProperties.Population pop = properties.population();
        String response = fetch(pop.serviceName(), pageNo);
        return parsePage(response, pop.serviceName(), node -> toRawPopulation(node, pop));
    }

    public SeoulDemographicsPage<SeoulRawForeignResident> fetchForeignResident(int pageNo) {
        SeoulDemographicsProperties.ForeignResident fr = properties.foreignResident();
        String response = fetch(fr.serviceName(), pageNo);
        return parsePage(response, fr.serviceName(), node -> toRawForeignResident(node, fr));
    }

    private String fetch(String serviceName, int pageNo) {
        int start = ((pageNo - 1) * properties.pageSize()) + 1;
        int end = pageNo * properties.pageSize();
        try {
            return restClient.get()
                    .uri(UriComponentsBuilder.fromUri(properties.baseUrl())
                            .pathSegment(properties.serviceKey(), "json", serviceName,
                                    String.valueOf(start), String.valueOf(end))
                            .build(false).toUri())
                    .retrieve()
                    .body(String.class);
        } catch (RestClientResponseException e) {
            boolean retryable = e.getStatusCode().is5xxServerError() || e.getStatusCode().value() == 429;
            throw new SeoulDemographicsApiException("Failed to call Seoul OpenData API", e, retryable);
        } catch (ResourceAccessException e) {
            throw new SeoulDemographicsApiException("Failed to call Seoul OpenData API", e, true);
        } catch (RestClientException e) {
            throw new SeoulDemographicsApiException("Failed to call Seoul OpenData API", e, false);
        }
    }

    private <T> SeoulDemographicsPage<T> parsePage(
            String source, String serviceName, Function<JsonNode, T> rowMapper
    ) {
        if (source == null || source.isBlank()) {
            throw new SeoulDemographicsApiException("Blank Seoul OpenData response", null, true);
        }
        try {
            JsonNode root = objectMapper.readTree(source);
            JsonNode dataRoot = root.path(serviceName);
            if (dataRoot.isMissingNode() || dataRoot.isNull()) {
                throw new SeoulDemographicsApiException("Missing dataset root in Seoul OpenData response");
            }
            validateResult(dataRoot);
            int totalCount = parseTotalCount(dataRoot);
            JsonNode rowNode = dataRoot.path("row");
            if (rowNode.isMissingNode() || rowNode.isNull()) {
                return new SeoulDemographicsPage<>(List.of(), totalCount);
            }
            List<T> rows = new ArrayList<>();
            rowNode.forEach(node -> rows.add(rowMapper.apply(node)));
            return new SeoulDemographicsPage<>(rows, totalCount);
        } catch (JsonProcessingException e) {
            throw new SeoulDemographicsApiException("Failed to parse Seoul OpenData response", e, true);
        }
    }

    private void validateResult(JsonNode dataRoot) {
        JsonNode codeNode = dataRoot.path("RESULT").path("CODE");
        if (!codeNode.isMissingNode() && !codeNode.asText().startsWith("INFO-")) {
            throw new SeoulDemographicsApiException(
                    "Seoul OpenData API returned error: " + codeNode.asText());
        }
    }

    private int parseTotalCount(JsonNode dataRoot) {
        String raw = dataRoot.path("list_total_count").asText("").trim();
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new SeoulDemographicsApiException("Invalid list_total_count: " + raw, e, false);
        }
    }

    private SeoulRawPopulation toRawPopulation(
            JsonNode node, SeoulDemographicsProperties.Population pop
    ) {
        return new SeoulRawPopulation(
                text(node, pop.sidoField()),
                text(node, pop.sigunguField()),
                text(node, pop.dongField()),
                text(node, pop.totalPopulationField()),
                text(node, pop.householdField()),
                text(node, pop.seniorField()),
                text(node, pop.referenceDateField())
        );
    }

    private SeoulRawForeignResident toRawForeignResident(
            JsonNode node, SeoulDemographicsProperties.ForeignResident fr
    ) {
        return new SeoulRawForeignResident(
                text(node, fr.sidoField()),
                text(node, fr.sigunguField()),
                text(node, fr.dongField()),
                text(node, fr.foreignCountField()),
                text(node, fr.referenceDateField())
        );
    }

    private String text(JsonNode node, String field) {
        JsonNode n = node.path(field);
        if (n.isMissingNode() || n.isNull()) return null;
        String v = n.asText().trim();
        return v.isEmpty() ? null : v;
    }
}
