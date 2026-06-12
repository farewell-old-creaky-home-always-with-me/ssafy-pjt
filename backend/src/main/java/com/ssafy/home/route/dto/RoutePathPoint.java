package com.ssafy.home.route.dto;

import java.math.BigDecimal;

public record RoutePathPoint(
        int seq,
        BigDecimal latitude,
        BigDecimal longitude,
        String name
) {}
