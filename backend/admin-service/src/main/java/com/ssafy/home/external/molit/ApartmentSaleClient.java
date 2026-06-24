package com.ssafy.home.external.molit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.home.batch.domain.HouseType;
import org.springframework.web.client.RestClient;

public class ApartmentSaleClient extends AbstractMolitHouseDealClient {
    public ApartmentSaleClient(
            RestClient restClient,
            ObjectMapper objectMapper,
            MolitProperties properties
    ) {
        super(restClient, objectMapper, properties,
                properties.apartmentSaleUrl(), HouseType.APARTMENT);
    }

    @Override
    protected String extractName(JsonNode item) {
        return text(item, "aptNm", "아파트");
    }
}
