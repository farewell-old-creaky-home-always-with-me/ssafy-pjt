package com.ssafy.home.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.home.auth.dto.AuthLoginRequest;
import com.ssafy.home.auth.dto.AuthMeResponse;
import com.ssafy.home.auth.dto.LoginResponse;
import com.ssafy.home.auth.service.AuthService;
import com.ssafy.home.global.auth.JwtProperties;
import com.ssafy.home.global.auth.JwtTokenProvider;
import com.ssafy.home.support.WebMvcTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(WebMvcTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        given(jwtProperties.accessTokenExpirationMillis()).willReturn(3_600_000L);
    }

    @Test
    @DisplayName("로그인 요청이 성공하면 200을 반환한다")
    void loginReturns200() throws Exception {
        // given
        AuthLoginRequest request = new AuthLoginRequest("user@example.com", "password123");
        given(authService.login(any(AuthLoginRequest.class)))
                .willReturn(new LoginResponse(1L, "tester", false, "access-token"));

        // when / then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(1L))
                .andExpect(jsonPath("$.name").value("tester"))
                .andExpect(jsonPath("$.accessToken").doesNotExist())
                .andExpect(cookie().httpOnly("access_token", true))
                .andExpect(cookie().secure("access_token", true))
                .andExpect(cookie().value("access_token", "access-token"));
    }

    @Test
    @DisplayName("인증 상태를 조회한다")
    void getAuthMeReturns200() throws Exception {
        // given
        given(authService.getAuthMe(any()))
                .willReturn(new AuthMeResponse(true, 1L, "tester", false));

        // when / then
        mockMvc.perform(get("/api/auth/me").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAuthenticated").value(true))
                .andExpect(jsonPath("$.memberId").value(1L));
    }

    @Test
    @DisplayName("로그아웃은 200을 반환한다")
    void logoutReturns200() throws Exception {
        // when / then
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(cookie().secure("access_token", true))
                .andExpect(cookie().maxAge("access_token", 0));
    }
}
