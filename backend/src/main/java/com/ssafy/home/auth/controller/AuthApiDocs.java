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

@Tag(name = "Auth", description = "Authentication API")
public interface AuthApiDocs {

    @Operation(
            summary = "Login",
            description = "Authenticate with email and password, then issue a JWT access token."
    )
    LoginResponse login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Login request", required = true)
            @Valid @RequestBody LoginRequest request
    );

    @Operation(
            summary = "Logout",
            description = "JWT logout is handled by deleting the token on the client."
    )
    ResponseEntity<Void> logout();

    @Operation(
            summary = "Current user",
            description = "Return the authenticated user from the JWT access token."
    )
    AuthMeResponse getAuthMe(@Parameter(hidden = true) HttpServletRequest request);
}
