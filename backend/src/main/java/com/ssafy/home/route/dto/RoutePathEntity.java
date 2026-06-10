package com.ssafy.home.route.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoutePathEntity {
    private Long routePathId;
    private Long routeRequestId;
    private int seq;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
