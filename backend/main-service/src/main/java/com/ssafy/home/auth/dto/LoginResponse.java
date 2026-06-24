package com.ssafy.home.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record LoginResponse(
        Long memberId,
        String name,
        boolean isAdmin,
        @JsonIgnore
        String accessToken
) {

    public static LoginResponse from(com.ssafy.home.member.mapper.dto.MemberDetailResult member) {
        return new LoginResponse(member.getId(), member.getName(), member.isAdmin(), null);
    }
}
