package com.ssafy.home.external.seoul.demographics;

public record SeoulRawPopulation(
        String sidoName,
        String sigunguName,
        String dongName,
        String totalPopulation,
        String householdCount,
        String seniorCount,
        String referenceDate
) {}
