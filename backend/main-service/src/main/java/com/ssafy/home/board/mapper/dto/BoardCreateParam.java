package com.ssafy.home.board.mapper.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardCreateParam {

    private Long id;
    private Long memberId;
    private String title;
    private String content;
}
