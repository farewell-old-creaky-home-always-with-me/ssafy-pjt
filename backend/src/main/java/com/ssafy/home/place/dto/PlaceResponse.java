package com.ssafy.home.place.dto;

import com.ssafy.home.place.mapper.dto.PlaceCreateParam;
import com.ssafy.home.place.mapper.dto.PlaceResult;
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
    public static PlaceResponse from(PlaceResult place) {
        return new PlaceResponse(
                place.getId(),
                place.getPlaceType(),
                place.getName(),
                place.getAddress(),
                place.getRegionCode(),
                place.getLatitude(),
                place.getLongitude(),
                place.getCreatedAt(),
                place.getUpdatedAt()
        );
    }

    public static PlaceResponse from(PlaceCreateParam place) {
        return new PlaceResponse(
                place.getId(),
                place.getPlaceType(),
                place.getName(),
                place.getAddress(),
                place.getRegionCode(),
                place.getLatitude(),
                place.getLongitude(),
                null,
                null
        );
    }
}
