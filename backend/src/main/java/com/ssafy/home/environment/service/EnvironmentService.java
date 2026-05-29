package com.ssafy.home.environment.service;

import com.ssafy.home.environment.dto.EnvironmentResponse;
import com.ssafy.home.environment.mapper.EnvironmentMapper;
import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.exception.ErrorCode;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnvironmentService {

    private final EnvironmentMapper environmentMapper;

    public List<EnvironmentResponse> getEnvironmentInfos(BigDecimal lat, BigDecimal lng, Integer radius) {
        validateCoordinate(lat, lng);
        int normalizedRadius = radius == null ? 1000 : radius;
        if (normalizedRadius <= 0) {
            throw new CustomException(ErrorCode.ENV_INVALID_RADIUS);
        }

        return environmentMapper.findEnvironmentInfos(lat, lng, normalizedRadius)
                .stream()
                .map(entity -> new EnvironmentResponse(
                        entity.getEnvId(),
                        entity.getItemName(),
                        entity.getValue(),
                        entity.getUnit(),
                        entity.getMeasuredDate(),
                        entity.getLatitude(),
                        entity.getLongitude(),
                        entity.getDistance()
                ))
                .toList();
    }

    private void validateCoordinate(BigDecimal lat, BigDecimal lng) {
        if (lat == null || lng == null
                || lat.compareTo(BigDecimal.valueOf(-90)) < 0
                || lat.compareTo(BigDecimal.valueOf(90)) > 0
                || lng.compareTo(BigDecimal.valueOf(-180)) < 0
                || lng.compareTo(BigDecimal.valueOf(180)) > 0) {
            throw new CustomException(ErrorCode.ENV_INVALID_COORDINATE);
        }
    }
}
