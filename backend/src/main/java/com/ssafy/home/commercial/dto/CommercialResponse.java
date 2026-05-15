package com.ssafy.home.commercial.dto;

import java.math.BigDecimal;

public record CommercialResponse(
        Long commercialId,
        String bizName,
        String categoryLarge,
        String categoryMedium,
        BigDecimal latitude,
        BigDecimal longitude,
        Double distance
) {
}
