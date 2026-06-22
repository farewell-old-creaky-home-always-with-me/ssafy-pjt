package com.ssafy.home.batch.processor;

import com.ssafy.home.batch.domain.NormalizedRegionCode;
import com.ssafy.home.external.vworld.VworldRawRegion;
import org.springframework.batch.item.ItemProcessor;

public class RegionCodeProcessor implements ItemProcessor<VworldRawRegion, NormalizedRegionCode> {

    @Override
    public NormalizedRegionCode process(VworldRawRegion item) {
        if (item.abolished()) {
            throw new InvalidRegionCodeException("Abolished region code");
        }
        String regionCode = required(item.regionCode(), "regionCode");
        if (!regionCode.matches("\\d{10}")) {
            throw new InvalidRegionCodeException("Invalid region code format");
        }
        if (regionCode.endsWith("00000")) {
            throw new InvalidRegionCodeException("Sigungu-level code is not supported");
        }
        return new NormalizedRegionCode(
                regionCode,
                required(item.sidoName(), "sidoName"),
                required(item.sigunguName(), "sigunguName"),
                required(item.dongName(), "dongName")
        );
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidRegionCodeException(field + " is required");
        }
        return value.trim();
    }
}
