package com.ssafy.home.external.molit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.home.batch.domain.HouseType;
import org.springframework.web.client.RestClient;

public class MultiFamilySaleClient extends AbstractMolitHouseDealClient {
    public MultiFamilySaleClient(
            RestClient restClient,
            ObjectMapper objectMapper,
            MolitProperties properties
    ) {
        super(restClient, objectMapper, properties,
                properties.multiFamilySaleUrl(), HouseType.MULTI_FAMILY);
    }

    @Override
    protected String extractName(JsonNode item) {
        String name = text(item, "mhouseNm", "연립다세대");
        return name == null ? text(item, "houseType", "주택유형") : name;
    }
}
