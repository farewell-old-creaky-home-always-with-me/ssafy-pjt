package com.ssafy.home.demographics.dto;

import com.ssafy.home.demographics.mapper.dto.DemographicsResult;

public record DemographicsResponse(
        String sidoName,
        String sigunguName,
        String dongName,
        Integer totalPopulation,
        Integer householdCount,
        Integer seniorCount,
        Integer foreignCount,
        String referenceDate
) {
    public static DemographicsResponse from(DemographicsResult result) {
        return new DemographicsResponse(
                result.getSidoName(),
                result.getSigunguName(),
                result.getDongName(),
                result.getTotalPopulation(),
                result.getHouseholdCount(),
                result.getSeniorCount(),
                result.getForeignCount(),
                result.getReferenceDate()
        );
    }
}
