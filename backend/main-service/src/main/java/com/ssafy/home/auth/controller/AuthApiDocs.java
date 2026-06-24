package com.ssafy.home.auth.controller;

import com.ssafy.home.auth.dto.AuthLoginRequest;
import com.ssafy.home.auth.dto.AuthMeResponse;
import com.ssafy.home.auth.dto.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "Authentication API")
public interface AuthApiDocs {

    @Operation(
            summary = "Login",
            description = "Authenticate with email and password, then issue a JWT access token in an HttpOnly cookie."
    )
    ResponseEntity<LoginResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Login request", required = true)
            @Valid @RequestBody AuthLoginRequest request
    );

    @Operation(
            summary = "Logout",
            description = "Clear the JWT access token cookie."
    )
    ResponseEntity<Void> logout();

    @Operation(
            summary = "Current user",
            description = "Return the authenticated user from the JWT access token cookie."
    )
    ResponseEntity<AuthMeResponse> getAuthMe(@Parameter(hidden = true) HttpServletRequest request);
}
