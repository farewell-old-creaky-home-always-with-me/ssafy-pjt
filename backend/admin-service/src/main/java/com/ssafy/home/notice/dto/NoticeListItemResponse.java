package com.ssafy.home.notice.dto;

import com.ssafy.home.notice.mapper.dto.NoticeResult;
import java.time.LocalDateTime;

public record NoticeListItemResponse(
        Long noticeId,
        String title,
        String authorName,
        LocalDateTime createdAt
) {

    public static NoticeListItemResponse from(NoticeResult notice) {
        return new NoticeListItemResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getAuthorName(),
                notice.getCreatedAt()
        );
    }
}
