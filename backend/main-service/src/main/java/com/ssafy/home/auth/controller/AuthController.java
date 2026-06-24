package com.ssafy.home.auth.controller;

import com.ssafy.home.auth.dto.AuthLoginRequest;
import com.ssafy.home.auth.dto.AuthMeResponse;
import com.ssafy.home.auth.dto.LoginResponse;
import com.ssafy.home.auth.service.AuthService;
import com.ssafy.home.global.auth.JwtProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApiDocs {

    private final AuthService authService;
    private final JwtProperties jwtProperties;
    private final boolean cookieSecure;

    public AuthController(
            AuthService authService,
            JwtProperties jwtProperties,
            @Value("${app.auth.cookie-secure:true}") boolean cookieSecure
    ) {
        this.authService = authService;
        this.jwtProperties = jwtProperties;
        this.cookieSecure = cookieSecure;
    }

    @PostMapping("/login")
    @Override
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie(response.accessToken()).toString())
                .body(response);
    }

    @PostMapping("/logout")
    @Override
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredAccessTokenCookie().toString())
                .build();
    }

    @GetMapping("/me")
    @Override
    public ResponseEntity<AuthMeResponse> getAuthMe(HttpServletRequest request) {
        return ResponseEntity.ok(authService.getAuthMe(request));
    }

    private ResponseCookie accessTokenCookie(String accessToken) {
        return ResponseCookie.from(AuthCookie.ACCESS_TOKEN, accessToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(jwtProperties.accessTokenExpirationMillis() / 1000)
                .build();
    }

    private ResponseCookie expiredAccessTokenCookie() {
        return ResponseCookie.from(AuthCookie.ACCESS_TOKEN, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
    }
}
