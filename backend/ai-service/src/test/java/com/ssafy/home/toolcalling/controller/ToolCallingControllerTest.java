package com.ssafy.home.toolcalling.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.home.global.auth.JwtProperties;
import com.ssafy.home.global.auth.JwtTokenProvider;
import com.ssafy.home.global.exception.GlobalExceptionHandler;
import com.ssafy.home.global.interceptor.AuthInterceptor;
import com.ssafy.home.toolcalling.dto.ToolChatRequest;
import com.ssafy.home.toolcalling.dto.ToolChatResponse;
import com.ssafy.home.toolcalling.dto.ToolTestResponse;
import com.ssafy.home.toolcalling.service.ToolCallingService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
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
class ToolCallingControllerTest {

    private static final String TEST_SECRET = "test-jwt-secret-key-for-ssafy-home-project-2026";

    @Mock
    private ToolCallingService toolCallingService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        JwtTokenProvider provider = new JwtTokenProvider(new JwtProperties(TEST_SECRET, 3600000L));
        mockMvc = MockMvcBuilders
            .standaloneSetup(new ToolCallingController(toolCallingService))
            .setValidator(validator)
            .setControllerAdvice(new GlobalExceptionHandler())
            .addInterceptors(new AuthInterceptor(provider))
            .build();
    }

    @Test
    void POST_api_ai_tools_chat_정상_응답을_반환한다() throws Exception {
        when(toolCallingService.chat("강남구 평균 거래가는?"))
            .thenReturn(new ToolChatResponse("AI 응답입니다.", true));

        mockMvc.perform(post("/api/ai/tools/chat")
                .header("Authorization", generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ToolChatRequest("강남구 평균 거래가는?"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.answer").value("AI 응답입니다."))
            .andExpect(jsonPath("$.toolCallingEnabled").value(true));
    }

    @Test
    void GET_api_ai_tools_test_정상_응답을_반환한다() throws Exception {
        when(toolCallingService.testTools())
            .thenReturn(new ToolTestResponse("StatsTool 결과", "HouseSearchTool 결과"));

        mockMvc.perform(get("/api/ai/tools/test")
                .header("Authorization", generateTestToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statsResult").value("StatsTool 결과"))
            .andExpect(jsonPath("$.houseSearchResult").value("HouseSearchTool 결과"));
    }

    @Test
    void Authorization_헤더_없이_tool_chat_요청하면_401을_반환한다() throws Exception {
        mockMvc.perform(post("/api/ai/tools/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ToolChatRequest("질문"))))
            .andExpect(status().isUnauthorized());
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
