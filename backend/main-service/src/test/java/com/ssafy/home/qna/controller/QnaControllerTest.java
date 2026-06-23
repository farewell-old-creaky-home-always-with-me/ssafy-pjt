package com.ssafy.home.qna.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.home.global.auth.JwtTokenProvider;
import com.ssafy.home.global.response.PageResponse;
import com.ssafy.home.qna.dto.QnaCreateRequest;
import com.ssafy.home.qna.dto.QnaDetailResponse;
import com.ssafy.home.qna.dto.QnaIdResponse;
import com.ssafy.home.qna.dto.QnaListItemResponse;
import com.ssafy.home.qna.dto.QnaStatus;
import com.ssafy.home.qna.dto.QnaUpdateRequest;
import com.ssafy.home.qna.service.QnaService;
import com.ssafy.home.support.WebMvcTestConfig;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(QnaController.class)
@Import(WebMvcTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class QnaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QnaService qnaService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("비로그인 상태로 QnA 목록을 조회한다")
    void getQnasReturns200WithoutToken() throws Exception {
        // given
        given(qnaService.getQnas(1, 20, QnaStatus.WAITING))
                .willReturn(PageResponse.of(List.of(qnaListItem()), 1L, 1, 20));

        // when / then
        mockMvc.perform(get("/api/qnas")
                        .param("status", "WAITING")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.items[0].qnaId").value(1L))
                .andExpect(jsonPath("$.items[0].status").value("WAITING"));
    }

    @Test
    @DisplayName("비로그인 상태로 QnA 상세를 조회한다")
    void getQnaReturns200WithoutToken() throws Exception {
        // given
        given(qnaService.getQna(1L)).willReturn(qnaDetail());

        // when / then
        mockMvc.perform(get("/api/qnas/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qnaId").value(1L))
                .andExpect(jsonPath("$.title").value("질문"));
    }

    @Test
    @DisplayName("토큰 없이 QnA를 등록하면 401을 반환한다")
    void createQnaReturns401WithoutToken() throws Exception {
        // when / then
        mockMvc.perform(post("/api/qnas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new QnaCreateRequest("질문", "내용"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_UNAUTHORIZED"));
    }

    @Test
    @DisplayName("로그인 토큰으로 QnA를 등록하면 201을 반환한다")
    void createQnaReturns201WithToken() throws Exception {
        // given
        authenticateMember(1L);
        given(qnaService.createQna(eq(1L), any(QnaCreateRequest.class))).willReturn(QnaIdResponse.of(3L));

        // when / then
        mockMvc.perform(post("/api/qnas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new QnaCreateRequest("질문", "내용"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.qnaId").value(3L));
    }

    @Test
    @DisplayName("빈 제목으로 QnA를 등록하면 400을 반환한다")
    void createQnaReturns400WhenTitleBlank() throws Exception {
        // given
        authenticateMember(1L);

        // when / then
        mockMvc.perform(post("/api/qnas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new QnaCreateRequest("", "내용"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COMMON_INVALID_INPUT"));
    }

    @Test
    @DisplayName("로그인 토큰으로 QnA를 수정한다")
    void updateQnaReturns200WithToken() throws Exception {
        // given
        authenticateMember(1L);
        given(qnaService.updateQna(eq(1L), eq(1L), any(QnaUpdateRequest.class))).willReturn(QnaIdResponse.of(1L));

        // when / then
        mockMvc.perform(put("/api/qnas/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new QnaUpdateRequest("수정", "수정 내용"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qnaId").value(1L));
    }

    @Test
    @DisplayName("로그인 토큰으로 QnA를 삭제한다")
    void deleteQnaReturns204WithToken() throws Exception {
        // given
        authenticateMember(1L);

        // when / then
        mockMvc.perform(delete("/api/qnas/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isNoContent());

        verify(qnaService).deleteQna(1L, 1L);
    }

    private QnaListItemResponse qnaListItem() {
        return new QnaListItemResponse(
                1L,
                "질문",
                "홍길동",
                QnaStatus.WAITING,
                LocalDateTime.of(2026, 6, 1, 10, 0),
                null
        );
    }

    private QnaDetailResponse qnaDetail() {
        return new QnaDetailResponse(
                1L,
                1L,
                "질문",
                "질문 내용",
                "홍길동",
                null,
                QnaStatus.WAITING,
                null,
                LocalDateTime.of(2026, 6, 1, 10, 0),
                null
        );
    }

    private void authenticateMember(Long memberId) {
        given(jwtTokenProvider.resolveToken(any())).willReturn("access-token");
        given(jwtTokenProvider.getMemberId("access-token")).willReturn(memberId);
        given(jwtTokenProvider.isAdmin("access-token")).willReturn(false);
    }
}
