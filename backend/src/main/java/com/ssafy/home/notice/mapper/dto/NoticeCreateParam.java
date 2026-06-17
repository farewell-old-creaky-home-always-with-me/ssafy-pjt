package com.ssafy.home.notice.mapper.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeCreateParam {

    private Long id;
    private Long memberId;
    private String title;
    private String content;
}
