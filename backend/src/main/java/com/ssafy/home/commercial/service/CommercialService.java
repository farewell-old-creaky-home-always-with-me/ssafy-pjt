package com.ssafy.home.commercial.service;

import com.ssafy.home.commercial.dto.CommercialResponse;
import com.ssafy.home.commercial.mapper.CommercialMapper;
import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.exception.ErrorCode;
import com.ssafy.home.global.response.ItemsResponse;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class CommercialService {

    private final CommercialMapper commercialMapper;

    public CommercialService(CommercialMapper commercialMapper) {
        this.commercialMapper = commercialMapper;
    }

    public ItemsResponse<CommercialResponse> getCommercials(
            BigDecimal lat,
            BigDecimal lng,
            Integer radius,
            String category
    ) {
        validateCoordinate(lat, lng);
        int normalizedRadius = radius == null ? 500 : radius;
        if (normalizedRadius <= 0) {
            throw new CustomException(ErrorCode.COMMERCIAL_INVALID_RADIUS);
        }

        return new ItemsResponse<>(commercialMapper.findCommercials(lat, lng, normalizedRadius, normalizeNullable(category))
                .stream()
                .map(entity -> new CommercialResponse(
                        entity.getCommercialId(),
                        entity.getBizName(),
                        entity.getCategoryLarge(),
                        entity.getCategoryMedium(),
                        entity.getLatitude(),
                        entity.getLongitude(),
                        entity.getDistance()
                ))
                .toList());
    }

    private void validateCoordinate(BigDecimal lat, BigDecimal lng) {
        if (lat == null || lng == null
                || lat.compareTo(BigDecimal.valueOf(-90)) < 0
                || lat.compareTo(BigDecimal.valueOf(90)) > 0
                || lng.compareTo(BigDecimal.valueOf(-180)) < 0
                || lng.compareTo(BigDecimal.valueOf(180)) > 0) {
            throw new CustomException(ErrorCode.COMMERCIAL_INVALID_COORDINATE);
        }
    }

    private String normalizeNullable(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
