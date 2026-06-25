package com.ssafy.home.demographics.service;

import static com.ssafy.home.global.exception.ErrorCode.DEMOGRAPHICS_NOT_FOUND;

import com.ssafy.home.demographics.dto.DemographicsResponse;
import com.ssafy.home.demographics.mapper.DemographicsMapper;
import com.ssafy.home.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DemographicsService {

    private final DemographicsMapper demographicsMapper;

    @Transactional(readOnly = true)
    public DemographicsResponse getDemographics(
            String sidoName, String sigunguName, String dongName
    ) {
        return demographicsMapper.findLatestByLocation(sidoName, sigunguName, dongName)
                .map(DemographicsResponse::from)
                .orElseThrow(() -> new CustomException(DEMOGRAPHICS_NOT_FOUND));
    }
}
