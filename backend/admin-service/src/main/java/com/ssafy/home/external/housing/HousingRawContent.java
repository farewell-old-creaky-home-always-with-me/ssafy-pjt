package com.ssafy.home.external.housing;

import java.time.LocalDateTime;

public record HousingRawContent(
        boolean information,
        String title,
        String body,
        String sourceUrl,
        String sourceName,
        String type,
        LocalDateTime publishedAt
) {
}
