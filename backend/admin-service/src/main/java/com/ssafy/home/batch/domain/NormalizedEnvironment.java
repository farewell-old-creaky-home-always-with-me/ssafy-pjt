package com.ssafy.home.batch.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NormalizedEnvironment(
        String itemName,
        BigDecimal value,
        String unit,
        LocalDate measuredDate,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
