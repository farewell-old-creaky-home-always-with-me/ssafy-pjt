package com.ssafy.home.commercial.dto;

import com.ssafy.home.commercial.mapper.dto.CommercialResult;
import java.math.BigDecimal;

public record CommercialResponse(
        Long commercialId,
        String bizName,
        String categoryLarge,
        String categoryMedium,
        BigDecimal latitude,
        BigDecimal longitude,
        Double distance
) {

    public static CommercialResponse from(CommercialResult entity) {
        return new CommercialResponse(
                entity.getId(),
                entity.getBizName(),
                entity.getCategoryLarge(),
                entity.getCategoryMedium(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getDistance()
        );
    }
}
