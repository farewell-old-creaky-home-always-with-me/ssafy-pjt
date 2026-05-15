package com.ssafy.home.auth.dto;

public record LoginResponse(
        Long memberId,
        String name,
        boolean isAdmin
) {
}
