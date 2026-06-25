package com.ssafy.home.news.dto;

import com.ssafy.home.news.mapper.dto.NewsResult;
import java.time.LocalDateTime;

public record NewsResponse(
        Long id,
        String title,
        String summary,
        String sourceName,
        String sourceUrl,
        String category,
        LocalDateTime publishedAt
) {

    public static NewsResponse from(NewsResult result) {
        return new NewsResponse(
                result.getId(),
                result.getTitle(),
                result.getSummary(),
                result.getSourceName(),
                result.getSourceUrl(),
                result.getCategory(),
                result.getPublishedAt()
        );
    }
}
