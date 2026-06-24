package com.ssafy.home.batch.domain;

import java.math.BigDecimal;

public record NormalizedCommercialArea(
        String bizId,
        String bizName,
        String categoryLarge,
        String categoryMedium,
        String categorySmall,
        BigDecimal latitude,
        BigDecimal longitude,
        String address
) {
}
