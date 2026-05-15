package com.ssafy.home.house.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record HouseSummaryResponse(
        Long houseId,
        String aptName,
        String jibun,
        Integer buildYear,
        String houseType,
        LatestDealResponse latestDeal
) {
    public record LatestDealResponse(
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
