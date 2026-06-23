package com.ssafy.home.qna.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.home.global.auth.JwtTokenProvider;
import com.ssafy.home.global.exception.GlobalExceptionHandler;
import com.ssafy.home.global.interceptor.AuthInterceptor;
import com.ssafy.home.qna.dto.QnaAnswerRequest;
import com.ssafy.home.qna.dto.QnaIdResponse;
import com.ssafy.home.qna.service.QnaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AdminQnaControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private QnaService qnaService;
    private JwtTokenProvider jwtTokenProvider;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        qnaService = mock(QnaService.class);
        jwtTokenProvider = mock(JwtTokenProvider.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new AdminQnaController(qnaService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .addInterceptors(new AuthInterceptor(jwtTokenProvider))
                .build();
    }

    @Test
    @DisplayName("토큰 없이 답변을 등록하면 401을 반환한다")
    void updateAnswerReturns401WithoutToken() throws Exception {
        // when / then
        mockMvc.perform(put("/api/qnas/1/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new QnaAnswerRequest("답변"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_UNAUTHORIZED"));
    }

    @Test
    @DisplayName("일반 사용자 토큰으로 답변을 등록하면 403을 반환한다")
    void updateAnswerReturns403ForNonAdminToken() throws Exception {
        // given
        authenticate(false);

        // when / then
        mockMvc.perform(put("/api/qnas/1/answer")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new QnaAnswerRequest("답변"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("AUTH_FORBIDDEN"));
    }

    @Test
    @DisplayName("관리자 토큰으로 답변을 등록하면 200을 반환한다")
    void updateAnswerReturns200ForAdminToken() throws Exception {
        // given
        authenticate(true);
        given(qnaService.updateAnswer(eq(1L), any(QnaAnswerRequest.class))).willReturn(QnaIdResponse.of(1L));

        // when / then
        mockMvc.perform(put("/api/qnas/1/answer")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new QnaAnswerRequest("답변"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qnaId").value(1L));
    }

    @Test
    @DisplayName("관리자 토큰으로 답변을 삭제하면 204를 반환한다")
    void deleteAnswerReturns204ForAdminToken() throws Exception {
        // given
        authenticate(true);

        // when / then
        mockMvc.perform(delete("/api/qnas/1/answer")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isNoContent());

        verify(qnaService).deleteAnswer(1L);
    }

    private void authenticate(boolean admin) {
        given(jwtTokenProvider.resolveToken(any())).willReturn("access-token");
        given(jwtTokenProvider.getMemberId("access-token")).willReturn(1L);
        given(jwtTokenProvider.isAdmin("access-token")).willReturn(admin);
    }
}
