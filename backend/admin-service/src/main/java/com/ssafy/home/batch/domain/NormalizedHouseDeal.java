package com.ssafy.home.batch.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NormalizedHouseDeal(
        String regionCode,
        String aptName,
        String jibun,
        Integer buildYear,
        String houseType,
        String dealType,
        Integer dealAmount,
        Integer depositAmount,
        Integer monthlyRent,
        LocalDate dealDate,
        BigDecimal area,
        Integer floor
) {
}
