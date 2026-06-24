package com.ssafy.home.report.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ssafy.home.global.auth.JwtTokenProvider;
import com.ssafy.home.report.dto.BatchReportResponse;
import com.ssafy.home.report.service.BatchReportQueryService;
import com.ssafy.home.support.WebMvcTestConfig;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BatchReportController.class)
@Import(WebMvcTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class BatchReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TempDir
    private Path tempDir;

    @MockitoBean
    private BatchReportQueryService batchReportQueryService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("최신 배치 보고서를 조회한다")
    void getLatestReport() throws Exception {
        authenticateMember(1L);
        given(batchReportQueryService.getLatestReport()).willReturn(response(1L));

        mockMvc.perform(get("/api/reports/batch/latest")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportId").value(1L))
                .andExpect(jsonPath("$.summary").value("요약"));
    }

    @Test
    @DisplayName("ID로 배치 보고서를 조회한다")
    void getReport() throws Exception {
        authenticateMember(1L);
        given(batchReportQueryService.getReport(1L)).willReturn(response(1L));

        mockMvc.perform(get("/api/reports/batch/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportId").value(1L))
                .andExpect(jsonPath("$.translatedSummary").value("Summary"));
    }

    @Test
    @DisplayName("배치 보고서 PDF를 조회한다")
    void downloadReportPdf() throws Exception {
        authenticateMember(1L);
        Path pdfPath = Files.writeString(tempDir.resolve("report.pdf"), "pdf");
        given(batchReportQueryService.getReportPdfFileName(1L)).willReturn("보고서 1.pdf");
        given(batchReportQueryService.getReportPdf(1L)).willReturn(pdfPath);

        mockMvc.perform(get("/api/reports/batch/1/pdf")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("filename*=")));
    }

    @Test
    @DisplayName("토큰 없이 배치 보고서를 조회하면 401을 반환한다")
    void getLatestReportReturns401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/reports/batch/latest").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_UNAUTHORIZED"));
    }

    private BatchReportResponse response(Long reportId) {
        return new BatchReportResponse(
                reportId,
                "REFLECTION",
                "HOUSE_DEAL",
                "11680",
                "202606",
                "요약",
                "Summary",
                "PDF_COMPLETED",
                "보고서.pdf",
                LocalDateTime.of(2026, 6, 24, 10, 0),
                LocalDateTime.of(2026, 6, 24, 10, 30)
        );
    }

    private void authenticateMember(Long memberId) {
        given(jwtTokenProvider.resolveToken(any())).willReturn("access-token");
        given(jwtTokenProvider.getMemberId("access-token")).willReturn(memberId);
        given(jwtTokenProvider.isAdmin("access-token")).willReturn(false);
    }
}
