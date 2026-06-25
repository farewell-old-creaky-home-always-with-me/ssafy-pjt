package com.ssafy.home.housinginfo.dto;

import com.ssafy.home.housinginfo.mapper.dto.HousingInfoResult;
import java.time.LocalDateTime;

public record HousingInfoResponse(
        Long id,
        String title,
        String content,
        String sourceName,
        String sourceUrl,
        String infoType,
        LocalDateTime publishedAt
) {

    public static HousingInfoResponse from(HousingInfoResult result) {
        return new HousingInfoResponse(
                result.getId(),
                result.getTitle(),
                result.getContent(),
                result.getSourceName(),
                result.getSourceUrl(),
                result.getInfoType(),
                result.getPublishedAt()
        );
    }
}
