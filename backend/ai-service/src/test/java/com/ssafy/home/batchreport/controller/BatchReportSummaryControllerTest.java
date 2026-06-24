package com.ssafy.home.batchreport.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.home.batchreport.dto.BatchReportSummaryRequest;
import com.ssafy.home.batchreport.dto.BatchReportSummaryResponse;
import com.ssafy.home.batchreport.dto.HouseDealSummaryItem;
import com.ssafy.home.batchreport.service.BatchReportSummaryService;
import com.ssafy.home.global.auth.JwtProperties;
import com.ssafy.home.global.auth.JwtTokenProvider;
import com.ssafy.home.global.exception.GlobalExceptionHandler;
import com.ssafy.home.global.interceptor.AuthInterceptor;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
class BatchReportSummaryControllerTest {

    private static final String TEST_SECRET = "test-jwt-secret-key-for-ssafy-home-project-2026";

    @Mock
    private BatchReportSummaryService batchReportSummaryService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        JwtTokenProvider provider = new JwtTokenProvider(new JwtProperties(TEST_SECRET, 3600000L));
        mockMvc = MockMvcBuilders
                .standaloneSetup(new BatchReportSummaryController(batchReportSummaryService))
                .setValidator(validator)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addInterceptors(new AuthInterceptor(provider))
                .build();
    }

    @Test
    void POST_api_ai_batch_reports_summary_정상_응답을_반환한다() throws Exception {
        when(batchReportSummaryService.summarize(any()))
                .thenReturn(new BatchReportSummaryResponse("요약", "Summary"));

        mockMvc.perform(post("/api/ai/batch/reports/summary")
                        .header("Authorization", generateTestToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").value("요약"))
                .andExpect(jsonPath("$.translatedSummary").value("Summary"));
    }

    @Test
    void Authorization_헤더_없이_batch_report_summary_요청하면_401을_반환한다() throws Exception {
        mockMvc.perform(post("/api/ai/batch/reports/summary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deals가_빈_목록이면_400을_반환한다() throws Exception {
        BatchReportSummaryRequest request = new BatchReportSummaryRequest(
                "11680",
                "202606",
                10L,
                1L,
                0,
                List.of()
        );

        mockMvc.perform(post("/api/ai/batch/reports/summary")
                        .header("Authorization", generateTestToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private BatchReportSummaryRequest request() {
        return new BatchReportSummaryRequest(
                "11680",
                "202606",
                10L,
                1L,
                0,
                List.of(new HouseDealSummaryItem(
                        "역삼래미안",
                        "역삼동",
                        "APARTMENT",
                        "SALE",
                        120000,
                        null,
                        BigDecimal.valueOf(84.5),
                        10
                ))
        );
    }

    private String generateTestToken() {
        Key signingKey = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
        return "Bearer " + Jwts.builder()
                .setSubject("1")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(signingKey)
                .compact();
    }
}
