package com.ssafy.home.batch.domain;

public record NormalizedPopulation(
        String sidoName,
        String sigunguName,
        String dongName,
        Integer totalPopulation,
        Integer householdCount,
        Integer seniorCount,
        String referenceDate
) {}
