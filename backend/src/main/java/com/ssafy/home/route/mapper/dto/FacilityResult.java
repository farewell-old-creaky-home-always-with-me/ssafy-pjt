package com.ssafy.home.route.mapper.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityResult {
    private Long facilityId;
    private String name;
    private String facilityType;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime createdAt;
}
