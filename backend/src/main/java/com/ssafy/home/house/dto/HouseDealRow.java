package com.ssafy.home.house.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HouseDealRow {

    private Long dealId;
    private String dealType;
    private Integer dealAmount;
    private Integer depositAmount;
    private Integer monthlyRent;
    private LocalDate dealDate;
    private BigDecimal area;
    private Integer floor;
}
