package com.ssafy.home.member.dto;

import java.time.LocalDateTime;

public record MemberDetailResponse(
        Long memberId,
        String email,
        String name,
        String phone,
        LocalDateTime createdAt
) {

    public static MemberDetailResponse from(com.ssafy.home.member.mapper.dto.MemberDetailResult member) {
        return new MemberDetailResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhone(),
                member.getCreatedAt()
        );
    }
}
