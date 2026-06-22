package com.ssafy.home.favorite.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.home.favorite.dto.FavoriteCreateRequest;
import com.ssafy.home.favorite.dto.FavoriteCreateResponse;
import com.ssafy.home.favorite.dto.FavoriteResponse;
import com.ssafy.home.favorite.service.FavoriteService;
import com.ssafy.home.global.auth.JwtTokenProvider;
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

@WebMvcTest(FavoriteController.class)
@Import(WebMvcTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FavoriteService favoriteService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("토큰 없이 관심 지역 목록을 조회하면 401을 반환한다")
    void getFavoritesReturns401WithoutToken() throws Exception {
        // when / then
        mockMvc.perform(get("/api/favorites").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_UNAUTHORIZED"));
    }

    @Test
    @DisplayName("로그인 토큰으로 관심 지역 목록을 조회한다")
    void getFavoritesReturns200WithToken() throws Exception {
        // given
        authenticateMember(1L);
        given(favoriteService.getFavorites(1L)).willReturn(List.of(favoriteResponse()));

        // when / then
        mockMvc.perform(get("/api/favorites")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].favoriteId").value(1L))
                .andExpect(jsonPath("$[0].regionCode").value("1168010100"));
    }

    @Test
    @DisplayName("관심 지역을 등록하면 201을 반환한다")
    void createFavoriteReturns201() throws Exception {
        // given
        FavoriteCreateRequest request = new FavoriteCreateRequest("1168010100");
        authenticateMember(1L);
        given(favoriteService.createFavorite(eq(1L), any(FavoriteCreateRequest.class)))
                .willReturn(new FavoriteCreateResponse(2L, "1168010100"));

        // when / then
        mockMvc.perform(post("/api/favorites")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.favoriteId").value(2L))
                .andExpect(jsonPath("$.regionCode").value("1168010100"));
    }

    private FavoriteResponse favoriteResponse() {
        return new FavoriteResponse(
                1L,
                "1168010100",
                "서울특별시",
                "강남구",
                "역삼동",
                LocalDateTime.of(2026, 6, 1, 9, 0)
        );
    }

    private void authenticateMember(Long memberId) {
        given(jwtTokenProvider.resolveToken(any())).willReturn("access-token");
        given(jwtTokenProvider.getMemberId("access-token")).willReturn(memberId);
        given(jwtTokenProvider.isAdmin("access-token")).willReturn(false);
    }
}
