package com.ssafy.home.notice.dto;

import com.ssafy.home.notice.mapper.dto.NoticeResult;
import java.time.LocalDateTime;

public record NoticeDetailResponse(
        Long noticeId,
        String title,
        String content,
        String authorName,
        LocalDateTime createdAt
) {

    public static NoticeDetailResponse from(NoticeResult notice) {
        return new NoticeDetailResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getAuthorName(),
                notice.getCreatedAt()
        );
    }
}
