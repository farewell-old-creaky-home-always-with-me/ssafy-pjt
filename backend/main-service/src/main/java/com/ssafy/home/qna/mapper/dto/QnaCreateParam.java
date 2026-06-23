package com.ssafy.home.qna.mapper.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QnaCreateParam {

    private Long id;
    private Long memberId;
    private String title;
    private String content;
}
