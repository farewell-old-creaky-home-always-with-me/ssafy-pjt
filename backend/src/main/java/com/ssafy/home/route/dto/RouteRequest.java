package com.ssafy.home.route.dto;

import jakarta.validation.constraints.NotNull;

public record RouteRequest(
        @NotNull Long houseId,
        @NotNull Long placeId
) {}
