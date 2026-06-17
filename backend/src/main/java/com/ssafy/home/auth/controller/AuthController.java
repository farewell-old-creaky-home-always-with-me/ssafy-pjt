package com.ssafy.home.auth.controller;

import com.ssafy.home.auth.dto.AuthMeResponse;
import com.ssafy.home.auth.dto.AuthLoginRequest;
import com.ssafy.home.auth.dto.LoginResponse;
import com.ssafy.home.auth.service.AuthService;
import com.ssafy.home.global.auth.SessionManager;
import com.ssafy.home.global.interceptor.LoginRequired;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApiDocs {

    private final AuthService authService;
    private final SessionManager sessionManager;

    @PostMapping("/login")
    @Override
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody AuthLoginRequest request,
            HttpServletRequest httpServletRequest
    ) {
        return ResponseEntity.ok(authService.login(request, httpServletRequest));
    }

    @LoginRequired
    @PostMapping("/logout")
    @Override
    public ResponseEntity<Void> logout() {
        sessionManager.invalidateCurrentSession();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @Override
    public ResponseEntity<AuthMeResponse> getAuthMe() {
        return ResponseEntity.ok(authService.getAuthMe());
    }
}
