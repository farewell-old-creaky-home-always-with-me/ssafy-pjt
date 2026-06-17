package com.ssafy.home.favorite.dto;

public record FavoriteCreateResponse(
        Long favoriteId,
        String regionCode
) {

    public static FavoriteCreateResponse of(Long id, String regionCode) {
        return new FavoriteCreateResponse(id, regionCode);
    }
}
