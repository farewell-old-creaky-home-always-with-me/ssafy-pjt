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
        String name = text(item.path("mhouseNm"));
        return name == null ? text(item.path("houseType")) : name;
    }
}
