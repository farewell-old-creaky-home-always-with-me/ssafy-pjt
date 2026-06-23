package com.ssafy.home.route.dto;

import com.ssafy.home.route.domain.Node;
import java.math.BigDecimal;

public record RoutePathPoint(
        int seq,
        BigDecimal latitude,
        BigDecimal longitude,
        String name
) {
    public static RoutePathPoint from(Node node, int seq) {
        return new RoutePathPoint(
                seq,
                BigDecimal.valueOf(node.getLat()),
                BigDecimal.valueOf(node.getLng()),
                node.getName()
        );
    }
}
