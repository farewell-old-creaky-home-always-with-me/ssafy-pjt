package com.ssafy.home.demographics.mapper.dto;

import lombok.Getter;

@Getter
public class DemographicsResult {
    private String sidoName;
    private String sigunguName;
    private String dongName;
    private Integer totalPopulation;
    private Integer householdCount;
    private Integer seniorCount;
    private Integer foreignCount;
    private String referenceDate;
}
