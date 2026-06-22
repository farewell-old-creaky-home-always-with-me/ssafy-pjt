package com.ssafy.home.global.interceptor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ssafy.home.global.auth.JwtProperties;
import com.ssafy.home.global.auth.JwtTokenProvider;
import com.ssafy.home.global.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class AuthInterceptorTest {

    private JwtTokenProvider jwtTokenProvider;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(new JwtProperties(
                "test-jwt-secret-key-for-ssafy-home-project-2026",
                3_600_000
        ));

        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .addInterceptors(new AuthInterceptor(jwtTokenProvider))
                .build();
    }

    @Test
    @DisplayName("토큰 없이 로그인 필수 API를 호출하면 401을 반환한다")
    void loginRequiredEndpointReturns401WithoutToken() throws Exception {
        // when / then
        mockMvc.perform(get("/login-required").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_UNAUTHORIZED"));
    }

    @Test
    @DisplayName("일반 회원 토큰으로 관리자 API를 호출하면 403을 반환한다")
    void adminOnlyEndpointReturns403ForNonAdminToken() throws Exception {
        // given
        String token = jwtTokenProvider.createAccessToken(1L, false);

        // when / then
        mockMvc.perform(get("/admin-only")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("AUTH_FORBIDDEN"));
    }

    @Test
    @DisplayName("관리자 토큰으로 관리자 API를 호출하면 통과한다")
    void adminOnlyEndpointPassesForAdminToken() throws Exception {
        // given
        String token = jwtTokenProvider.createAccessToken(1L, true);

        // when / then
        mockMvc.perform(get("/admin-only")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @RestController
    static class TestController {

        @LoginRequired
        @GetMapping("/login-required")
        public String loginRequired() {
            return "ok";
        }

        @AdminOnly
        @GetMapping("/admin-only")
        public String adminOnly() {
            return "ok";
        }
    }
}
