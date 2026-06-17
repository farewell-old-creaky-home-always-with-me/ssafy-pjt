package com.ssafy.home.auth.dto;

public record LoginResponse(
        Long memberId,
        String name,
        boolean isAdmin
) {

    public static LoginResponse from(com.ssafy.home.member.mapper.dto.MemberDetailResult member) {
        return new LoginResponse(member.getId(), member.getName(), member.isAdmin());
    }
}
