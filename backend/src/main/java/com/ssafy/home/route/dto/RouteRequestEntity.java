package com.ssafy.home.route.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteRequestEntity {
    private Long routeRequestId;
    private Long memberId;
    private Long houseId;
    private Long placeId;
    private int totalDistM;
    private int nodeCount;
    private LocalDateTime createdAt;
}
