package com.ssafy.home.auth.dto;

public record AuthMeResponse(
        boolean isAuthenticated,
        Long memberId,
        String name,
        Boolean isAdmin
) {
}
