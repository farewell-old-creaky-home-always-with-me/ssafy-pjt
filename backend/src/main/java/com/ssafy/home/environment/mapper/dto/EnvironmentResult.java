package com.ssafy.home.environment.mapper.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnvironmentResult {

    private Long id;
    private String itemName;
    private BigDecimal value;
    private String unit;
    private LocalDate measuredDate;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Double distance;
}
