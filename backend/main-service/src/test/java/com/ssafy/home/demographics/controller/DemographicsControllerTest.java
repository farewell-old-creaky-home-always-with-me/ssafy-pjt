package com.ssafy.home.demographics.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ssafy.home.demographics.dto.DemographicsResponse;
import com.ssafy.home.demographics.service.DemographicsService;
import com.ssafy.home.global.auth.JwtTokenProvider;
import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.exception.ErrorCode;
import com.ssafy.home.support.WebMvcTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DemographicsController.class)
@Import(WebMvcTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class DemographicsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DemographicsService demographicsService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("동네 구성원 통계를 조회한다")
    void getDemographicsReturns200() throws Exception {
        // Given
        given(demographicsService.getDemographics("서울특별시", "강남구", "역삼1동"))
                .willReturn(demographicsResponse());

        // When / Then
        mockMvc.perform(get("/api/demographics")
                        .param("sido", "서울특별시")
                        .param("sigungu", "강남구")
                        .param("dong", "역삼1동")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sidoName").value("서울특별시"))
                .andExpect(jsonPath("$.sigunguName").value("강남구"))
                .andExpect(jsonPath("$.dongName").value("역삼1동"))
                .andExpect(jsonPath("$.totalPopulation").value(12345))
                .andExpect(jsonPath("$.householdCount").value(5678))
                .andExpect(jsonPath("$.seniorCount").value(1234))
                .andExpect(jsonPath("$.foreignCount").value(345))
                .andExpect(jsonPath("$.referenceDate").value("202505"));
    }

    @Test
    @DisplayName("데이터가 없는 동을 조회하면 404를 반환한다")
    void getDemographicsReturns404WhenNotFound() throws Exception {
        // Given
        given(demographicsService.getDemographics("서울특별시", "강남구", "없는동"))
                .willThrow(new CustomException(ErrorCode.DEMOGRAPHICS_NOT_FOUND));

        // When / Then
        mockMvc.perform(get("/api/demographics")
                        .param("sido", "서울특별시")
                        .param("sigungu", "강남구")
                        .param("dong", "없는동")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("DEMOGRAPHICS_NOT_FOUND"));
    }

    @Test
    @DisplayName("sido 파라미터가 없으면 400을 반환한다")
    void getDemographicsReturns400WhenSidoMissing() throws Exception {
        mockMvc.perform(get("/api/demographics")
                        .param("sigungu", "강남구")
                        .param("dong", "역삼1동")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private DemographicsResponse demographicsResponse() {
        return new DemographicsResponse(
                "서울특별시", "강남구", "역삼1동", 12345, 5678, 1234, 345, "202505");
    }
}
