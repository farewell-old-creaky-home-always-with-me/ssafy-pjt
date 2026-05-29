package com.ssafy.home.house.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HouseDetailRow {

    private Long houseId;
    private String aptName;
    private String regionCode;
    private String jibun;
    private Integer buildYear;
    private String houseType;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
