package com.ssafy.home.batchreport.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record HouseDealSummaryItem(
        String aptName,
        String dongName,
        String houseType,
        String dealType,
        Integer dealAmount,
        LocalDate dealDate,
        BigDecimal area,
        Integer floor
) {
}
