package com.ssafy.home.favorite.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoriteEntity {

    private Long favoriteId;
    private Long memberId;
    private String regionCode;
    private String sidoName;
    private String sigunguName;
    private String dongName;
    private LocalDateTime createdAt;
}
