package com.ssafy.home.commercial.service;

import com.ssafy.home.commercial.dto.CommercialResponse;
import com.ssafy.home.commercial.mapper.CommercialMapper;
import com.ssafy.home.global.exception.CustomException;
import static com.ssafy.home.global.exception.ErrorCode.COMMERCIAL_INVALID_COORDINATE;
import static com.ssafy.home.global.exception.ErrorCode.COMMERCIAL_INVALID_RADIUS;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommercialService {

    private final CommercialMapper commercialMapper;

    @Transactional(readOnly = true)
    public List<CommercialResponse> getCommercials(
            BigDecimal lat,
            BigDecimal lng,
            Integer radius,
            String category
    ) {
        validateCoordinate(lat, lng);
        int normalizedRadius = radius == null ? 500 : radius;
        if (normalizedRadius <= 0) {
            throw new CustomException(COMMERCIAL_INVALID_RADIUS);
        }

        return commercialMapper.findAllByLocation(lat, lng, normalizedRadius, normalizeNullable(category))
                .stream()
                .map(CommercialResponse::from)
                .toList();
    }

    private void validateCoordinate(BigDecimal lat, BigDecimal lng) {
        if (lat == null || lng == null
                || lat.compareTo(BigDecimal.valueOf(-90)) < 0
                || lat.compareTo(BigDecimal.valueOf(90)) > 0
                || lng.compareTo(BigDecimal.valueOf(-180)) < 0
                || lng.compareTo(BigDecimal.valueOf(180)) > 0) {
            throw new CustomException(COMMERCIAL_INVALID_COORDINATE);
        }
    }

    private String normalizeNullable(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
