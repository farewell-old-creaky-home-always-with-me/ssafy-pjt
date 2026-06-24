package com.ssafy.home.house.mapper.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HouseSummaryResult {

    private Long houseId;
    private String aptName;
    private String jibun;
    private Integer buildYear;
    private String houseType;
    private String regionCode;
    private String dongName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String latestDealType;
    private Integer latestDealAmount;
    private Integer latestDepositAmount;
    private Integer latestMonthlyRent;
    private LocalDate latestDealDate;
    private BigDecimal latestArea;
    private Integer latestFloor;
}
