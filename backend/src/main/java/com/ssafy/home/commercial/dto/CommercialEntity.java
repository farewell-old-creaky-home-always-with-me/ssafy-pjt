package com.ssafy.home.commercial.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommercialEntity {

    private Long commercialId;
    private String bizName;
    private String categoryLarge;
    private String categoryMedium;
    private String categorySmall;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private Double distance;
    private LocalDateTime createdAt;
}
