package com.ssafy.home.board.controller;

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
import com.ssafy.home.board.dto.BoardCreateRequest;
import com.ssafy.home.board.dto.BoardDetailResponse;
import com.ssafy.home.board.dto.BoardIdResponse;
import com.ssafy.home.board.dto.BoardListItemResponse;
import com.ssafy.home.board.dto.BoardUpdateRequest;
import com.ssafy.home.board.service.BoardService;
import com.ssafy.home.global.auth.JwtTokenProvider;
import com.ssafy.home.global.response.PageResponse;
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

@WebMvcTest(BoardController.class)
@Import(WebMvcTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BoardService boardService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("비로그인 상태로 게시글 목록을 조회한다")
    void getBoardsReturns200WithoutToken() throws Exception {
        // given
        given(boardService.getBoards(1, 20))
                .willReturn(PageResponse.of(List.of(boardListItem()), 1L, 1, 20));

        // when / then
        mockMvc.perform(get("/api/boards").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.items[0].boardId").value(1L))
                .andExpect(jsonPath("$.items[0].title").value("title"));
    }

    @Test
    @DisplayName("비로그인 상태로 게시글 상세를 조회한다")
    void getBoardReturns200WithoutToken() throws Exception {
        // given
        given(boardService.getBoard(1L)).willReturn(boardDetail());

        // when / then
        mockMvc.perform(get("/api/boards/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boardId").value(1L))
                .andExpect(jsonPath("$.content").value("content"));
    }

    @Test
    @DisplayName("토큰 없이 게시글을 등록하면 401을 반환한다")
    void createBoardReturns401WithoutToken() throws Exception {
        // when / then
        mockMvc.perform(post("/api/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BoardCreateRequest("title", "content"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_UNAUTHORIZED"));
    }

    @Test
    @DisplayName("로그인 토큰으로 게시글을 등록하면 201을 반환한다")
    void createBoardReturns201WithToken() throws Exception {
        // given
        authenticateMember(1L);
        given(boardService.createBoard(eq(1L), any(BoardCreateRequest.class))).willReturn(BoardIdResponse.of(3L));

        // when / then
        mockMvc.perform(post("/api/boards")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BoardCreateRequest("title", "content"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.boardId").value(3L));
    }

    @Test
    @DisplayName("빈 제목으로 게시글을 등록하면 400을 반환한다")
    void createBoardReturns400WhenTitleBlank() throws Exception {
        // given
        authenticateMember(1L);

        // when / then
        mockMvc.perform(post("/api/boards")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BoardCreateRequest("", "content"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COMMON_INVALID_INPUT"));
    }

    @Test
    @DisplayName("로그인 토큰으로 게시글을 수정한다")
    void updateBoardReturns200WithToken() throws Exception {
        // given
        authenticateMember(1L);
        given(boardService.updateBoard(eq(1L), eq(1L), any(BoardUpdateRequest.class))).willReturn(BoardIdResponse.of(1L));

        // when / then
        mockMvc.perform(put("/api/boards/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BoardUpdateRequest("updated", "updated content"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boardId").value(1L));
    }

    @Test
    @DisplayName("로그인 토큰으로 게시글을 삭제한다")
    void deleteBoardReturns204WithToken() throws Exception {
        // given
        authenticateMember(1L);

        // when / then
        mockMvc.perform(delete("/api/boards/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isNoContent());

        verify(boardService).deleteBoard(1L, 1L);
    }

    private BoardListItemResponse boardListItem() {
        return new BoardListItemResponse(
                1L,
                1L,
                "title",
                "User One",
                LocalDateTime.of(2026, 6, 1, 10, 0),
                null
        );
    }

    private BoardDetailResponse boardDetail() {
        return new BoardDetailResponse(
                1L,
                1L,
                "title",
                "content",
                "User One",
                LocalDateTime.of(2026, 6, 1, 10, 0),
                null
        );
    }

    private void authenticateMember(Long memberId) {
        given(jwtTokenProvider.resolveToken(any())).willReturn("access-token");
        given(jwtTokenProvider.getMemberId("access-token")).willReturn(memberId);
    }
}
