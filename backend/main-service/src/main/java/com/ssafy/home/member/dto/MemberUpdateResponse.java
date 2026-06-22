package com.ssafy.home.member.dto;

public record MemberUpdateResponse(
        Long memberId,
        String name
) {

    public static MemberUpdateResponse of(Long memberId, String name) {
        return new MemberUpdateResponse(memberId, name);
    }
}
