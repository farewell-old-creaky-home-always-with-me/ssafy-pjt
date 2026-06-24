package com.ssafy.home.batch.report.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HouseDealReportRow {
    private String aptName;
    private String dongName;
    private String houseType;
    private String dealType;
    private Integer dealAmount;
    private LocalDate dealDate;
    private BigDecimal area;
    private Integer floor;
}
