package com.ssafy.home.member.controller;

import com.ssafy.home.global.auth.LoginMemberId;
import com.ssafy.home.member.dto.CreateMemberRequest;
import com.ssafy.home.member.dto.MemberResponse;
import com.ssafy.home.member.dto.MemberUpdateResponse;
import com.ssafy.home.member.dto.UpdateMemberRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Member", description = "회원 API")
public interface MemberApiDocs {

    @Operation(
            summary = "회원 가입",
            description = "회원 정보를 등록합니다."
    )
    ResponseEntity<MemberResponse> createMember(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "회원 가입 요청", required = true)
            @Valid @RequestBody CreateMemberRequest request
    );

    @Operation(
            summary = "내 회원 정보 조회",
            description = "현재 로그인한 회원의 정보를 조회합니다."
    )
    MemberResponse getMyMember(@Parameter(hidden = true) @LoginMemberId Long memberId);

    @Operation(
            summary = "내 회원 정보 수정",
            description = "현재 로그인한 회원의 정보를 수정합니다."
    )
    MemberUpdateResponse updateMyMember(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "회원 수정 요청", required = true)
            @Valid @RequestBody UpdateMemberRequest request,
            @Parameter(hidden = true) @LoginMemberId Long memberId
    );

    @Operation(
            summary = "내 회원 탈퇴",
            description = "현재 로그인한 회원을 삭제하고 세션을 종료합니다."
    )
    ResponseEntity<Void> deleteMyMember(@Parameter(hidden = true) @LoginMemberId Long memberId);
}
