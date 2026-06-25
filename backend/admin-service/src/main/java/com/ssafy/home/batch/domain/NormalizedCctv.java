package com.ssafy.home.batch.domain;

import java.math.BigDecimal;

public record NormalizedCctv(
        String purpose,
        Integer cameraCount,
        String address,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
