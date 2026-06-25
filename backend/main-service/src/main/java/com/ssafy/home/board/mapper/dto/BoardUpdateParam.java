package com.ssafy.home.board.mapper.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardUpdateParam {

    private Long id;
    private String title;
    private String content;
}
