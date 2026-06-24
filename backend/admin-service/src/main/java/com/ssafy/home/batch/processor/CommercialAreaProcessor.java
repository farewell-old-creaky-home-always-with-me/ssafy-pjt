package com.ssafy.home.batch.processor;

import com.ssafy.home.batch.domain.NormalizedCommercialArea;
import com.ssafy.home.external.sdsc.SdscRawStore;
import java.math.BigDecimal;
import org.springframework.batch.item.ItemProcessor;

public class CommercialAreaProcessor implements ItemProcessor<SdscRawStore, NormalizedCommercialArea> {

    @Override
    public NormalizedCommercialArea process(SdscRawStore item) {
        String bizId = required(item.bizesId(), "bizesId");
        String bizName = required(item.bizesNm(), "bizesNm");
        BigDecimal latitude = parseCoordinate(item.lat(), "lat");
        BigDecimal longitude = parseCoordinate(item.lon(), "lon");
        String address = firstNonBlank(item.rdnmAdr(), item.lnoAdr());
        return new NormalizedCommercialArea(
                bizId,
                bizName,
                item.indsLclsNm(),
                item.indsMclsNm(),
                item.indsSclsNm(),
                latitude,
                longitude,
                address
        );
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidCommercialAreaException(field + " is required");
        }
        return value.trim();
    }

    private BigDecimal parseCoordinate(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidCommercialAreaException(field + " is required");
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            throw new InvalidCommercialAreaException(field + " is not a valid number: " + value);
        }
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
}
