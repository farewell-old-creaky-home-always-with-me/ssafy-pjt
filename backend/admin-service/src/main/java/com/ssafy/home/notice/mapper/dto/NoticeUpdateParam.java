package com.ssafy.home.notice.mapper.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeUpdateParam {

    private Long id;
    private String title;
    private String content;
}
