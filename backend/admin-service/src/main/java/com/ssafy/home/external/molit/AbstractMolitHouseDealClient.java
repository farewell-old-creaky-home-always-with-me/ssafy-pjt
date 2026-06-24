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
            return parse(response, regionCode);
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

    private MolitHouseDealPage parse(String source, String lawdCode) {
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
                return new MolitHouseDealPage(List.of(), integer(body.path("totalCount")));
            }
            List<MolitRawHouseDeal> deals = new ArrayList<>();
            if (item.isArray()) {
                item.forEach(node -> deals.add(toRaw(node, lawdCode)));
            } else if (item.isObject()) {
                deals.add(toRaw(item, lawdCode));
            }
            return new MolitHouseDealPage(deals, integer(body.path("totalCount")));
        } catch (JsonProcessingException exception) {
            throw new MolitApiException("Failed to parse MOLIT response", exception, true);
        }
    }

    private MolitRawHouseDeal toRaw(JsonNode item, String lawdCode) {
        return new MolitRawHouseDeal(
                legalDongCode(item),
                lawdCode,
                text(item, "umdNm", "법정동"),
                extractName(item), text(item, "jibun", "지번"),
                text(item, "dealAmount", "거래금액"),
                text(item, "dealYear", "년"),
                text(item, "dealMonth", "월"),
                text(item, "dealDay", "일"),
                text(item, "excluUseAr", "전용면적"),
                text(item, "floor", "층"),
                text(item, "buildYear", "건축년도")
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

    protected String text(JsonNode item, String... fieldNames) {
        for (String fieldName : fieldNames) {
            String value = text(item.path(fieldName));
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private String legalDongCode(JsonNode item) {
        String sgg = text(item, "sggCd", "법정동시군구코드");
        String umd = text(item, "umdCd", "법정동읍면동코드");
        if (sgg == null || !sgg.matches("\\d{5}")
                || umd == null || !umd.matches("\\d{1,5}")) {
            return null;
        }
        return sgg + "0".repeat(5 - umd.length()) + umd;
    }

    private int integer(JsonNode node) {
        String text = node.asText("0").trim();
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            throw new MolitApiException(
                    "Unexpected totalCount format from MOLIT API: '" + text + "'",
                    exception,
                    true
            );
        }
    }
}
