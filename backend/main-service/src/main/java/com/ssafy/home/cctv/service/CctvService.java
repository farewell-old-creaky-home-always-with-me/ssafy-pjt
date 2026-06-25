package com.ssafy.home.cctv.service;

import static com.ssafy.home.global.exception.ErrorCode.CCTV_INVALID_COORDINATE;
import static com.ssafy.home.global.exception.ErrorCode.CCTV_INVALID_RADIUS;

import com.ssafy.home.cctv.dto.CctvResponse;
import com.ssafy.home.cctv.mapper.CctvMapper;
import com.ssafy.home.global.exception.CustomException;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CctvService {

    private final CctvMapper cctvMapper;

    @Transactional(readOnly = true)
    public List<CctvResponse> getCctvInfos(BigDecimal lat, BigDecimal lng, Integer radius) {
        validateCoordinate(lat, lng);
        int normalizedRadius = radius == null ? 1000 : radius;
        if (normalizedRadius <= 0) {
            throw new CustomException(CCTV_INVALID_RADIUS);
        }
        return cctvMapper.findAllByLocation(lat, lng, normalizedRadius)
                .stream()
                .map(CctvResponse::from)
                .toList();
    }

    private void validateCoordinate(BigDecimal lat, BigDecimal lng) {
        if (lat == null || lng == null
                || lat.compareTo(BigDecimal.valueOf(-90)) < 0
                || lat.compareTo(BigDecimal.valueOf(90)) > 0
                || lng.compareTo(BigDecimal.valueOf(-180)) < 0
                || lng.compareTo(BigDecimal.valueOf(180)) > 0) {
            throw new CustomException(CCTV_INVALID_COORDINATE);
        }
    }
}
