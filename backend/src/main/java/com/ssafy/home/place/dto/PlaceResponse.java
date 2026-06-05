package com.ssafy.home.place.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PlaceResponse(
        Long placeId,
        String placeType,
        String name,
        String address,
        String regionCode,
        BigDecimal latitude,
        BigDecimal longitude,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
