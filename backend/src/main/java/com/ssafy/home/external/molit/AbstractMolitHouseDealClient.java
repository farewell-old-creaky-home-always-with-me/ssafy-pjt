package com.ssafy.home.external.molit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.home.batch.domain.HouseType;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class AbstractMolitHouseDealClient implements MolitHouseDealClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final MolitProperties properties;
    private final URI endpoint;
    private final HouseType supportedType;

    protected AbstractMolitHouseDealClient(
            RestClient restClient,
            ObjectMapper objectMapper,
            MolitProperties properties,
            URI endpoint,
            HouseType supportedType
    ) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.properties = properties;
        this.endpoint = endpoint;
        this.supportedType = supportedType;
    }

    @Override
    public boolean supports(HouseType houseType) {
        return supportedType == houseType;
    }

    @Override
    public MolitHouseDealPage fetch(String regionCode, String yearMonth, int pageNumber) {
        try {
            String response = restClient.get()
                    .uri(requestUri(regionCode, yearMonth, pageNumber))
                    .retrieve()
                    .body(String.class);
            return parse(response);
        } catch (RestClientResponseException exception) {
            boolean retryable = exception.getStatusCode().is5xxServerError()
                    || exception.getStatusCode().value() == 429;
            throw new MolitApiException("Failed to call MOLIT API", exception, retryable);
        } catch (ResourceAccessException exception) {
            throw new MolitApiException("Failed to call MOLIT API", exception, true);
        } catch (RestClientException exception) {
            throw new MolitApiException("Failed to call MOLIT API", exception, false);
        }
    }

    private URI requestUri(String regionCode, String yearMonth, int pageNumber) {
        return UriComponentsBuilder.fromUri(endpoint)
                .queryParam("serviceKey", properties.serviceKey())
                .queryParam("LAWD_CD", regionCode)
                .queryParam("DEAL_YMD", yearMonth)
                .queryParam("pageNo", pageNumber)
                .queryParam("numOfRows", properties.pageSize())
                .queryParam("_type", "json")
                .build(true)
                .toUri();
    }

    private MolitHouseDealPage parse(String source) {
        if (source == null || source.isBlank()) {
            throw new MolitApiException("Blank MOLIT response", null, true);
        }
        try {
            JsonNode response = objectMapper.readTree(source).path("response");
            JsonNode header = response.path("header");
            String code = text(header.path("resultCode"));
            if (!"000".equals(code) && !"00".equals(code)) {
                throw new MolitApiException(code, text(header.path("resultMsg")));
            }
            JsonNode body = response.path("body");
            JsonNode item = body.path("items").path("item");
            if (item.isMissingNode() || item.isNull()) {
                return MolitHouseDealPage.empty();
            }
            List<MolitRawHouseDeal> deals = new ArrayList<>();
            if (item.isArray()) {
                item.forEach(node -> deals.add(toRaw(node)));
            } else if (item.isObject()) {
                deals.add(toRaw(item));
            }
            return new MolitHouseDealPage(deals, integer(body.path("totalCount")));
        } catch (JsonProcessingException exception) {
            throw new MolitApiException("Failed to parse MOLIT response", exception, true);
        }
    }

    private MolitRawHouseDeal toRaw(JsonNode item) {
        return new MolitRawHouseDeal(
                legalDongCode(item), extractName(item), text(item.path("jibun")),
                text(item.path("dealAmount")), text(item.path("dealYear")),
                text(item.path("dealMonth")), text(item.path("dealDay")),
                text(item.path("excluUseAr")), text(item.path("floor")),
                text(item.path("buildYear"))
        );
    }

    protected abstract String extractName(JsonNode item);

    protected String text(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        String value = node.asText().trim();
        return value.isEmpty() ? null : value;
    }

    private String legalDongCode(JsonNode item) {
        String sgg = text(item.path("sggCd"));
        String umd = text(item.path("umdCd"));
        if (sgg == null || !sgg.matches("\\d{5}")
                || umd == null || !umd.matches("\\d{1,5}")) {
            return null;
        }
        return sgg + "0".repeat(5 - umd.length()) + umd;
    }

    private int integer(JsonNode node) {
        try {
            return Integer.parseInt(node.asText("0").trim());
        } catch (NumberFormatException exception) {
            return 0;
        }
    }
}
