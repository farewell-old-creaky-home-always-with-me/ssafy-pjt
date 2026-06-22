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

    private static final String SIGUNGU_LAYER = "LT_C_ADSIGG_INFO";
    private static final String NATIONAL_GEOM_FILTER = "BOX(124,33,132,39)";
    private static final URI WFS_URL = URI.create("https://api.vworld.kr/req/wfs");

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

    public List<String> fetchSigunguCodes(String sidoCode) {
        List<String> sigunguCodes = new ArrayList<>();
        int pageNumber = 1;
        while (true) {
            VworldRegionPage page = fetchSigunguPage(pageNumber);
            page.regions().stream()
                    .map(VworldRawRegion::regionCode)
                    .filter(code -> code != null && code.startsWith(sidoCode))
                    .forEach(sigunguCodes::add);
            if (page.regions().isEmpty() || isLastPage(pageNumber, page)) {
                break;
            }
            pageNumber++;
        }
        return sigunguCodes;
    }

    public VworldRegionPage fetch(String sigunguCode, int pageNumber) {
        try {
            String response = restClient.get()
                    .uri(wfsRequestUri(sigunguCode, pageNumber))
                    .retrieve()
                    .body(String.class);
            return parseWfs(response);
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

    private VworldRegionPage fetchSigunguPage(int pageNumber) {
        try {
            String response = restClient.get()
                    .uri(dataRequestUri(SIGUNGU_LAYER, pageNumber))
                    .retrieve()
                    .body(String.class);
            return parseData(response);
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

    private URI dataRequestUri(String layer, int pageNumber) {
        return UriComponentsBuilder.fromUri(properties.dataUrl())
                .queryParam("service", "data")
                .queryParam("request", "GetFeature")
                .queryParam("version", "2.0")
                .queryParam("key", properties.apiKey())
                .queryParam("domain", properties.domain())
                .queryParam("format", "json")
                .queryParam("size", properties.pageSize())
                .queryParam("page", pageNumber)
                .queryParam("data", layer)
                .queryParam("geometry", "false")
                .queryParam("attribute", "true")
                .queryParam("geomFilter", NATIONAL_GEOM_FILTER)
                .queryParam("crs", "EPSG:4326")
                .build(false)
                .toUri();
    }

    private URI wfsRequestUri(String sigunguCode, int pageNumber) {
        String filter = """
                <Filter><PropertyIsLike wildCard="*" singleChar="." escape="!">\
                <PropertyName>emd_cd</PropertyName><Literal>%s*</Literal></PropertyIsLike></Filter>\
                """.formatted(sigunguCode);
        int startIndex = (pageNumber - 1) * properties.pageSize();
        return UriComponentsBuilder.fromUri(WFS_URL)
                .queryParam("key", properties.apiKey())
                .queryParam("domain", properties.domain())
                .queryParam("SERVICE", "WFS")
                .queryParam("REQUEST", "GetFeature")
                .queryParam("VERSION", "1.1.0")
                .queryParam("TYPENAME", properties.legalEmdLayer().toLowerCase())
                .queryParam("OUTPUT", "application/json")
                .queryParam("SRSNAME", "EPSG:4326")
                .queryParam("MAXFEATURES", properties.pageSize())
                .queryParam("STARTINDEX", startIndex)
                .queryParam("FILTER", filter)
                .build(false)
                .toUri();
    }

    private VworldRegionPage parseData(String source) {
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

    private VworldRegionPage parseWfs(String source) {
        if (source == null || source.isBlank()) {
            throw new VworldApiException("Blank VWorld response", null, true);
        }
        try {
            JsonNode root = objectMapper.readTree(source);
            JsonNode features = root.path("features");
            List<VworldRawRegion> regions = new ArrayList<>();
            if (features.isArray()) {
                features.forEach(node -> regions.add(toRaw(node.path("properties"))));
            }
            int totalCount = root.path("totalFeatures").asInt(regions.size());
            return new VworldRegionPage(regions, totalCount);
        } catch (JsonProcessingException exception) {
            throw new VworldApiException("Failed to parse VWorld response", exception, true);
        }
    }

    private VworldRawRegion toRaw(JsonNode properties) {
        String sigCd = text(properties.path("sig_cd"));
        String emdCd = text(properties.path("emd_cd"));
        String bjcd = text(properties.path("bjcd"));
        String regionCode = toLegalRegionCode(bjcd, emdCd, sigCd);
        RegionNames names = resolveNames(properties);
        return new VworldRawRegion(
                regionCode,
                names.sidoName(),
                names.sigunguName(),
                names.dongName(),
                isAbolished(properties)
        );
    }

    private String toLegalRegionCode(String bjcd, String emdCd, String sigCd) {
        if (bjcd != null && bjcd.matches("\\d{10}")) {
            return bjcd;
        }
        if (emdCd != null && emdCd.matches("\\d{10}")) {
            return emdCd;
        }
        if (emdCd != null && emdCd.matches("\\d{8}")) {
            return emdCd + "00";
        }
        return sigCd;
    }

    private RegionNames resolveNames(JsonNode properties) {
        String dongName = firstNonBlank(
                text(properties.path("emd_nm")),
                text(properties.path("emd_kor_nm"))
        );
        String sidoName = text(properties.path("sido_nm"));
        String sigunguName = firstNonBlank(
                text(properties.path("sgg_nm")),
                text(properties.path("sig_kor_nm"))
        );
        if (sidoName != null && sigunguName != null && dongName != null) {
            return new RegionNames(sidoName, sigunguName, dongName);
        }
        return parseFullName(text(properties.path("full_nm")), dongName);
    }

    private RegionNames parseFullName(String fullName, String dongName) {
        if (fullName == null || fullName.isBlank()) {
            return new RegionNames(null, null, dongName);
        }
        String prefix = fullName.trim();
        if (dongName != null && !dongName.isBlank() && prefix.endsWith(dongName)) {
            prefix = prefix.substring(0, prefix.length() - dongName.length()).trim();
        }
        if (prefix.isEmpty()) {
            return new RegionNames(null, null, dongName);
        }
        String[] parts = prefix.split("\\s+");
        String sidoName = parts[0];
        String sigunguName = parts.length > 1 ? parts[1] : null;
        return new RegionNames(sidoName, sigunguName, dongName);
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

    private boolean isLastPage(int pageNumber, VworldRegionPage page) {
        return page.regions().size() < properties.pageSize()
                || pageNumber * properties.pageSize() >= page.totalCount();
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

    private record RegionNames(String sidoName, String sigunguName, String dongName) {
    }
}
