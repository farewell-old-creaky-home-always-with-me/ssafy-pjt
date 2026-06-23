package com.ssafy.home.environment.dto;

import com.ssafy.home.environment.mapper.dto.EnvironmentResult;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EnvironmentResponse(
        Long envId,
        String itemName,
        BigDecimal value,
        String unit,
        LocalDate measuredDate,
        BigDecimal latitude,
        BigDecimal longitude,
        Double distance
) {

    public static EnvironmentResponse from(EnvironmentResult entity) {
        return new EnvironmentResponse(
                entity.getId(),
                entity.getItemName(),
                entity.getValue(),
                entity.getUnit(),
                entity.getMeasuredDate(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getDistance()
        );
    }
}
