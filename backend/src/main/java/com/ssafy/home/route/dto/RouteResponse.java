package com.ssafy.home.route.dto;

import java.util.List;

public record RouteResponse(
        Long routeRequestId,
        int totalDistanceM,
        List<RoutePathPoint> path
) {}
