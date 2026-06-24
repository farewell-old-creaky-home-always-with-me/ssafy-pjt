package com.ssafy.home.external.molit;

public record MolitRawHouseDeal(
        String legalDongCode,
        String lawdCode,
        String legalDongName,
        String name,
        String jibun,
        String dealAmount,
        String dealYear,
        String dealMonth,
        String dealDay,
        String area,
        String floor,
        String buildYear
) {
}
