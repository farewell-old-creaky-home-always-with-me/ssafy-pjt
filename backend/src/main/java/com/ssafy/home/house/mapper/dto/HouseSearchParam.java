package com.ssafy.home.house.mapper.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HouseSearchParam {

    private String regionCode;
    private String houseType;
    private String dealType;
    private Integer minAmount;
    private Integer maxAmount;
    private int page;
    private int size;
    private int offset;
}
