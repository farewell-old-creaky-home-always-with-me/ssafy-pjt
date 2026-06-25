package com.ssafy.home.batch.domain;

import java.time.LocalDateTime;

public record NormalizedHousingNews(
        String title,
        String summary,
        String sourceUrl,
        String sourceName,
        HousingNewsCategory category,
        LocalDateTime publishedAt
) implements NormalizedHousingContent {
}
