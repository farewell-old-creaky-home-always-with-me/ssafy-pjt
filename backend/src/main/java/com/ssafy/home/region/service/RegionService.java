package com.ssafy.home.region.service;

import com.ssafy.home.region.dto.RegionResponse;
import com.ssafy.home.region.mapper.RegionMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionMapper regionMapper;

    @Transactional(readOnly = true)
    public List<RegionResponse> getRegions(String dong) {
        String normalizedDong = (dong == null || dong.isBlank()) ? null : dong.trim();
        return regionMapper.findAll(normalizedDong)
                .stream()
                .map(RegionResponse::from)
                .toList();
    }
}
