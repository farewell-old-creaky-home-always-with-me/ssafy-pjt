package com.ssafy.home.notice.dto;

import java.time.LocalDateTime;

public record NoticeDetailResponse(
        Long noticeId,
        String title,
        String content,
        String authorName,
        LocalDateTime createdAt
) {
}
