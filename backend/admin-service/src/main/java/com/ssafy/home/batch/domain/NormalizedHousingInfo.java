package com.ssafy.home.batch.domain;

import java.time.LocalDateTime;

public record NormalizedHousingInfo(
        String title,
        String content,
        String sourceUrl,
        String sourceName,
        HousingInfoType infoType,
        LocalDateTime publishedAt
) implements NormalizedHousingContent {
}
