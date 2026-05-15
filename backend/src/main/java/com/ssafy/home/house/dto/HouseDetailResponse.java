package com.ssafy.home.house.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record HouseDetailResponse(
        Long houseId,
        String aptName,
        String regionCode,
        String jibun,
        Integer buildYear,
        String houseType,
        BigDecimal latitude,
        BigDecimal longitude,
        List<HouseDealResponse> deals
) {
    public record HouseDealResponse(
            Long dealId,
            String dealType,
            Integer dealAmount,
            Integer depositAmount,
            Integer monthlyRent,
            LocalDate dealDate,
            BigDecimal area,
            Integer floor
    ) {
    }
}
