package com.ssafy.home.notice.dto;

public record NoticeIdResponse(Long noticeId) {

    public static NoticeIdResponse of(Long noticeId) {
        return new NoticeIdResponse(noticeId);
    }
}
