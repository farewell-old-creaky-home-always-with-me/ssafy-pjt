package com.ssafy.home.member.dto;

import java.time.LocalDateTime;

public record MemberResponse(
        Long memberId,
        String email,
        String name,
        LocalDateTime createdAt
) {
}
