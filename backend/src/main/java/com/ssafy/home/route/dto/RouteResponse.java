package com.ssafy.home.route.dto;

import java.util.List;

public record RouteResponse(
        Long routeRequestId,
        int totalDistanceM,
        List<RoutePathPoint> path
) {
    public static RouteResponse of(Long routeRequestId, int totalDistanceM, List<RoutePathPoint> path) {
        return new RouteResponse(routeRequestId, totalDistanceM, path);
    }
}
