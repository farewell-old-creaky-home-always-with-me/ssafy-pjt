package com.ssafy.home.notice.dto;

import java.time.LocalDateTime;

public record NoticeListItemResponse(
        Long noticeId,
        String title,
        String authorName,
        LocalDateTime createdAt
) {
}
