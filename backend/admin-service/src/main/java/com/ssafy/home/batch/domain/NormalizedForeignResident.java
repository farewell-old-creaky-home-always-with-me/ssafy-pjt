package com.ssafy.home.batch.domain;

public record NormalizedForeignResident(
        String sidoName,
        String sigunguName,
        String dongName,
        Integer foreignCount,
        String referenceDate
) {}
