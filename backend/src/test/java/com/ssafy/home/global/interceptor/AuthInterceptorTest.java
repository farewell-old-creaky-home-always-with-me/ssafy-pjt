package com.ssafy.home.global.interceptor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ssafy.home.global.exception.GlobalExceptionHandler;
import com.ssafy.home.global.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
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
    void loginRequiredEndpointReturns401WithoutSession() throws Exception {
        mockMvc.perform(get("/login-required").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("AUTH_UNAUTHORIZED"));
    }

    @Test
    void adminOnlyEndpointReturns403ForNonAdmin() throws Exception {
        mockMvc.perform(get("/admin-only")
                        .sessionAttr("memberId", 1L)
                        .sessionAttr("isAdmin", false)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("AUTH_FORBIDDEN"));
    }

    @Test
    void adminOnlyEndpointPassesForAdminSession() throws Exception {
        mockMvc.perform(get("/admin-only")
                        .sessionAttr("memberId", 1L)
                        .sessionAttr("isAdmin", true)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @RestController
    static class TestController {

        @LoginRequired
        @GetMapping("/login-required")
        public ApiResponse<String> loginRequired() {
            return ApiResponse.success("ok");
        }

        @AdminOnly
        @GetMapping("/admin-only")
        public ApiResponse<String> adminOnly() {
            return ApiResponse.success("ok");
        }
    }
}
