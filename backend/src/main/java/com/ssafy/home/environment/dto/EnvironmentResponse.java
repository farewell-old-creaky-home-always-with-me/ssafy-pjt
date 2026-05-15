package com.ssafy.home.environment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EnvironmentResponse(
        Long envId,
        String itemName,
        BigDecimal value,
        String unit,
        LocalDate measuredDate,
        BigDecimal latitude,
        BigDecimal longitude,
        Double distance
) {
}
