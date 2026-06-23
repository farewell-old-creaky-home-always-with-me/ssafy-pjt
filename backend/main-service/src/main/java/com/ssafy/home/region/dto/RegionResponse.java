package com.ssafy.home.region.dto;

import com.ssafy.home.region.mapper.dto.RegionResult;

public record RegionResponse(
        String regionCode,
        String sidoName,
        String sigunguName,
        String dongName
) {
    public static RegionResponse from(RegionResult result) {
        return new RegionResponse(
                result.getRegionCode(),
                result.getSidoName(),
                result.getSigunguName(),
                result.getDongName()
        );
    }
}
