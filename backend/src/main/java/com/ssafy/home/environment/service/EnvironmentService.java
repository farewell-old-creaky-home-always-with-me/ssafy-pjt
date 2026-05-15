package com.ssafy.home.environment.service;

import com.ssafy.home.environment.dto.EnvironmentResponse;
import com.ssafy.home.environment.mapper.EnvironmentMapper;
import com.ssafy.home.global.exception.ValidationException;
import com.ssafy.home.global.response.ItemsResponse;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class EnvironmentService {

    private final EnvironmentMapper environmentMapper;

    public EnvironmentService(EnvironmentMapper environmentMapper) {
        this.environmentMapper = environmentMapper;
    }

    public ItemsResponse<EnvironmentResponse> getEnvironmentInfos(BigDecimal lat, BigDecimal lng, Integer radius) {
        validateCoordinate(lat, lng);
        int normalizedRadius = radius == null ? 1000 : radius;
        if (normalizedRadius <= 0) {
            throw new ValidationException("ENV_INVALID_COORDINATE", "반경은 0보다 커야 합니다");
        }

        return new ItemsResponse<>(environmentMapper.findEnvironmentInfos(lat, lng, normalizedRadius)
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
                .toList());
    }

    private void validateCoordinate(BigDecimal lat, BigDecimal lng) {
        if (lat == null || lng == null
                || lat.compareTo(BigDecimal.valueOf(-90)) < 0
                || lat.compareTo(BigDecimal.valueOf(90)) > 0
                || lng.compareTo(BigDecimal.valueOf(-180)) < 0
                || lng.compareTo(BigDecimal.valueOf(180)) > 0) {
            throw new ValidationException("ENV_INVALID_COORDINATE", "유효하지 않은 좌표입니다");
        }
    }
}
