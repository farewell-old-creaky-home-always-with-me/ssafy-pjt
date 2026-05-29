package com.ssafy.home.environment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnvironmentEntity {

    private Long envId;
    private String itemName;
    private BigDecimal value;
    private String unit;
    private LocalDate measuredDate;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Double distance;
}
