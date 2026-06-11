package com.ssafy.home.route.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RouteRequest(
        @NotNull @Positive Long houseId,
        @NotNull @Positive Long placeId
) {}
