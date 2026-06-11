package com.ssafy.home.auth.controller;

import com.ssafy.home.auth.dto.AuthMeResponse;
import com.ssafy.home.auth.dto.LoginRequest;
import com.ssafy.home.auth.dto.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApiDocs {

    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인하고 세션을 생성합니다."
    )
    LoginResponse login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "로그인 요청", required = true)
            @Valid @RequestBody LoginRequest request,
            @Parameter(hidden = true) HttpServletRequest httpServletRequest
    );

    @Operation(
            summary = "로그아웃",
            description = "현재 로그인 세션을 종료합니다."
    )
    ResponseEntity<Void> logout();

    @Operation(
            summary = "내 인증 정보 조회",
            description = "현재 세션의 로그인 사용자 정보를 조회합니다."
    )
    AuthMeResponse getAuthMe(@Parameter(hidden = true) HttpServletRequest request);
}
