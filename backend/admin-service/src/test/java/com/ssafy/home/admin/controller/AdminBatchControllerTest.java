package com.ssafy.home.admin.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ssafy.home.admin.dto.DemographicsCollectResponse;
import com.ssafy.home.admin.dto.EnvironmentCollectResponse;
import com.ssafy.home.admin.dto.HousingNewsCollectResponse;
import com.ssafy.home.admin.service.BatchJobService;
import com.ssafy.home.batch.report.BatchReportFileService;
import com.ssafy.home.global.auth.JwtTokenProvider;
import com.ssafy.home.global.auth.LoginMemberIdArgumentResolver;
import com.ssafy.home.global.exception.GlobalExceptionHandler;
import com.ssafy.home.global.interceptor.AuthInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AdminBatchControllerTest {

    private BatchJobService batchJobService;
    private JwtTokenProvider jwtTokenProvider;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        batchJobService = mock(BatchJobService.class);
        BatchReportFileService batchReportFileService = mock(BatchReportFileService.class);
        jwtTokenProvider = mock(JwtTokenProvider.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new AdminBatchController(batchJobService, batchReportFileService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new LoginMemberIdArgumentResolver())
                .addInterceptors(new AuthInterceptor(jwtTokenProvider))
                .build();
    }

    @Test
    @DisplayName("관리자 토큰으로 환경 정보 수집 배치를 실행한다")
    void collectEnvironmentReturns200ForAdminToken() throws Exception {
        // Given
        authenticate(true);
        given(batchJobService.collectEnvironment(1L))
                .willReturn(new EnvironmentCollectResponse(9L, "environmentCollectJob", "STARTING"));

        // When / Then
        mockMvc.perform(post("/api/admin/batch/environment")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.executionId").value(9L))
                .andExpect(jsonPath("$.jobName").value("environmentCollectJob"))
                .andExpect(jsonPath("$.status").value("STARTING"));

        verify(batchJobService).collectEnvironment(1L);
    }

    @Test
    @DisplayName("관리자 토큰으로 주거 뉴스 수집 배치를 실행한다")
    void collectHousingNewsReturns200ForAdminToken() throws Exception {
        // Given
        authenticate(true);
        given(batchJobService.collectHousingNews(1L))
                .willReturn(new HousingNewsCollectResponse(10L, "housingNewsCollectJob", "STARTING"));

        // When / Then
        mockMvc.perform(post("/api/admin/batch/news")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.executionId").value(10L))
                .andExpect(jsonPath("$.jobName").value("housingNewsCollectJob"))
                .andExpect(jsonPath("$.status").value("STARTING"));

        verify(batchJobService).collectHousingNews(1L);
    }

    @Test
    @DisplayName("일반 사용자 토큰으로 환경 정보 수집 배치를 실행하면 403을 반환한다")
    void collectEnvironmentReturns403ForNonAdminToken() throws Exception {
        // Given
        authenticate(false);

        // When / Then
        mockMvc.perform(post("/api/admin/batch/environment")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("AUTH_FORBIDDEN"));
    }

    @Test
    @DisplayName("관리자 토큰으로 동네 구성원 수집 배치를 실행한다")
    void collectDemographicsReturns200ForAdminToken() throws Exception {
        // Given
        authenticate(true);
        given(batchJobService.collectDemographics(1L))
                .willReturn(new DemographicsCollectResponse(10L, "demographicsCollectJob", "STARTING"));

        // When / Then
        mockMvc.perform(post("/api/admin/batch/demographics")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.executionId").value(10L))
                .andExpect(jsonPath("$.jobName").value("demographicsCollectJob"))
                .andExpect(jsonPath("$.status").value("STARTING"));

        verify(batchJobService).collectDemographics(1L);
    }

    @Test
    @DisplayName("일반 사용자 토큰으로 동네 구성원 수집 배치를 실행하면 403을 반환한다")
    void collectDemographicsReturns403ForNonAdminToken() throws Exception {
        // Given
        authenticate(false);

        // When / Then
        mockMvc.perform(post("/api/admin/batch/demographics")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isForbidden());
    }

    private void authenticate(boolean admin) {
        given(jwtTokenProvider.resolveToken(any())).willReturn("access-token");
        given(jwtTokenProvider.getMemberId("access-token")).willReturn(1L);
        given(jwtTokenProvider.isAdmin("access-token")).willReturn(admin);
    }
}
