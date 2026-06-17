package com.ssafy.home.global.interceptor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ssafy.home.global.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class AuthInterceptorTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .addInterceptors(new AuthInterceptor())
                .build();
    }

    @Test
    @DisplayName("세션 없이 로그인 필수 API를 호출하면 401을 반환한다")
    void loginRequiredEndpointReturns401WithoutSession() throws Exception {
        // when / then
        mockMvc.perform(get("/login-required").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_UNAUTHORIZED"));
    }

    @Test
    @DisplayName("일반 회원이 관리자 전용 API를 호출하면 403을 반환한다")
    void adminOnlyEndpointReturns403ForNonAdmin() throws Exception {
        // when / then
        mockMvc.perform(get("/admin-only")
                        .sessionAttr("memberId", 1L)
                        .sessionAttr("isAdmin", false)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("AUTH_FORBIDDEN"));
    }

    @Test
    @DisplayName("관리자 세션으로 관리자 전용 API를 호출하면 통과한다")
    void adminOnlyEndpointPassesForAdminSession() throws Exception {
        // when / then
        mockMvc.perform(get("/admin-only")
                        .sessionAttr("memberId", 1L)
                        .sessionAttr("isAdmin", true)
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
