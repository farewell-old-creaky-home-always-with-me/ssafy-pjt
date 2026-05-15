package com.ssafy.home.favorite.dto;

import java.time.LocalDateTime;

public record FavoriteResponse(
        Long favoriteId,
        String regionCode,
        String sidoName,
        String sigunguName,
        String dongName,
        LocalDateTime createdAt
) {
}
