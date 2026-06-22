package com.ssafy.home.place.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PlaceUpdateRequest(
        @NotBlank String placeType,
        @NotBlank String name,
        @NotBlank String address,
        String regionCode,
        @NotNull BigDecimal latitude,
        @NotNull BigDecimal longitude
) {
}
