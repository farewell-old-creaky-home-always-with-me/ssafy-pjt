package com.ssafy.home.region.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ssafy.home.global.auth.JwtTokenProvider;
import com.ssafy.home.region.dto.RegionResponse;
import com.ssafy.home.region.service.RegionService;
import com.ssafy.home.support.WebMvcTestConfig;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RegionController.class)
@Import(WebMvcTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class RegionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegionService regionService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("dong 파라미터 없이 전체 지역 목록을 조회한다")
    void getRegionsWithoutDongReturns200() throws Exception {
        given(regionService.getRegions(null))
                .willReturn(List.of(
                        new RegionResponse("1168010100", "서울특별시", "강남구", "역삼동")
                ));

        mockMvc.perform(get("/api/regions").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].regionCode").value("1168010100"))
                .andExpect(jsonPath("$[0].sidoName").value("서울특별시"))
                .andExpect(jsonPath("$[0].dongName").value("역삼동"));
    }

    @Test
    @DisplayName("dong 파라미터로 동 이름을 검색한다")
    void getRegionsWithDongReturns200() throws Exception {
        given(regionService.getRegions("역삼"))
                .willReturn(List.of(
                        new RegionResponse("1168010100", "서울특별시", "강남구", "역삼동")
                ));

        mockMvc.perform(get("/api/regions")
                        .param("dong", "역삼")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].regionCode").value("1168010100"))
                .andExpect(jsonPath("$[0].dongName").value("역삼동"));
    }

    @Test
    @DisplayName("매칭되는 지역이 없으면 빈 배열을 반환한다")
    void getRegionsWithNoMatchReturnsEmpty() throws Exception {
        given(regionService.getRegions("없는동")).willReturn(List.of());

        mockMvc.perform(get("/api/regions")
                        .param("dong", "없는동")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
