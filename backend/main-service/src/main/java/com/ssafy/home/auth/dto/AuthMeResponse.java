package com.ssafy.home.auth.dto;

public record AuthMeResponse(
        boolean isAuthenticated,
        Long memberId,
        String name,
        Boolean isAdmin
) {

    public static AuthMeResponse from(com.ssafy.home.member.mapper.dto.MemberDetailResult member) {
        return new AuthMeResponse(true, member.getId(), member.getName(), member.isAdmin());
    }

    public static AuthMeResponse guest() {
        return new AuthMeResponse(false, null, null, null);
    }
}
