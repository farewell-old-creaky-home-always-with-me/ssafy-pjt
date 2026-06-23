package com.ssafy.home.qna.mapper.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QnaUpdateParam {

    private Long id;
    private String title;
    private String content;
}
