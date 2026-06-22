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

    public static FavoriteResponse from(com.ssafy.home.favorite.mapper.dto.FavoriteDetailResult favorite) {
        return new FavoriteResponse(
                favorite.getId(),
                favorite.getRegionCode(),
                favorite.getSidoName(),
                favorite.getSigunguName(),
                favorite.getDongName(),
                favorite.getCreatedAt()
        );
    }
}
