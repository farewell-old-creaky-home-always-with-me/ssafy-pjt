package com.ssafy.home.favorite.mapper.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoriteDetailResult {

    private Long id;
    private Long memberId;
    private String regionCode;
    private String sidoName;
    private String sigunguName;
    private String dongName;
    private LocalDateTime createdAt;
}
