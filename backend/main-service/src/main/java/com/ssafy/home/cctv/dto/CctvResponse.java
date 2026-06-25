package com.ssafy.home.cctv.dto;

import com.ssafy.home.cctv.mapper.dto.CctvResult;
import java.math.BigDecimal;

public record CctvResponse(
        Long cctvId,
        String purpose,
        Integer cameraCount,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        Double distance
) {
    public static CctvResponse from(CctvResult result) {
        return new CctvResponse(
                result.getId(),
                result.getPurpose(),
                result.getCameraCount(),
                result.getAddress(),
                result.getLatitude(),
                result.getLongitude(),
                result.getDistance()
        );
    }
}
