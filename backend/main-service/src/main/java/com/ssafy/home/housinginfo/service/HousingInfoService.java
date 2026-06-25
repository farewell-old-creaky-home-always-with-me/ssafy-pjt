package com.ssafy.home.housinginfo.service;

import com.ssafy.home.housinginfo.dto.HousingInfoResponse;
import com.ssafy.home.housinginfo.mapper.HousingInfoMapper;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HousingInfoService {

    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 50;

    private final HousingInfoMapper housingInfoMapper;

    @Transactional(readOnly = true)
    public List<HousingInfoResponse> getHousingInfo(String type, Integer limit) {
        return housingInfoMapper.findRecent(normalizeType(type), normalizeLimit(limit))
                .stream()
                .map(HousingInfoResponse::from)
                .toList();
    }

    private String normalizeType(String type) {
        if (type == null) {
            return null;
        }

        String normalized = type.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        return normalized.toUpperCase(Locale.ROOT);
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }
}
