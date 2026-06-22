package com.ssafy.home.favorite.mapper.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoriteCreateParam {

    private Long id;
    private Long memberId;
    private String regionCode;
}
