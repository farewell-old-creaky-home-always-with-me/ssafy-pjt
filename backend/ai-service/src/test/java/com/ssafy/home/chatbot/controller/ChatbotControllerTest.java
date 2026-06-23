package com.ssafy.home.chatbot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.home.chatbot.dto.ChatRequest;
import com.ssafy.home.chatbot.dto.ChatResponse;
import com.ssafy.home.chatbot.dto.SearchResponse;
import com.ssafy.home.chatbot.service.ChatbotService;
import com.ssafy.home.chatbot.service.DocumentService;
import com.ssafy.home.global.auth.JwtProperties;
import com.ssafy.home.global.auth.JwtTokenProvider;
import com.ssafy.home.global.exception.GlobalExceptionHandler;
import com.ssafy.home.global.interceptor.AuthInterceptor;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ChatbotControllerTest {

    @Mock private ChatbotService chatbotService;
    @Mock private DocumentService documentService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String TEST_SECRET = "test-jwt-secret-key-for-ssafy-home-project-2026";

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        JwtProperties props = new JwtProperties(TEST_SECRET, 3600000L);
        JwtTokenProvider provider = new JwtTokenProvider(props);
        AuthInterceptor authInterceptor = new AuthInterceptor(provider);
        mockMvc = MockMvcBuilders
            .standaloneSetup(new ChatbotController(chatbotService, documentService))
            .setValidator(validator)
            .setControllerAdvice(new GlobalExceptionHandler())
            .addInterceptors(authInterceptor)
            .build();
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

    @Test
    void POST_api_chat_정상_응답을_반환한다() throws Exception {
        when(chatbotService.chat("아파트 가격")).thenReturn(new ChatResponse("AI 응답입니다.", true));

        mockMvc.perform(post("/api/chat")
                .header("Authorization", generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ChatRequest("아파트 가격"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.answer").value("AI 응답입니다."))
            .andExpect(jsonPath("$.ragUsed").value(true));
    }

    @Test
    void POST_api_chat_message가_빈_문자열이면_400을_반환한다() throws Exception {
        mockMvc.perform(post("/api/chat")
                .header("Authorization", generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ChatRequest(""))))
            .andExpect(status().isBadRequest());
    }

    @Test
    void POST_api_chat_upload_정상_업로드된다() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.txt", "text/plain", "내용".getBytes()
        );

        mockMvc.perform(multipart("/api/chat/upload")
                .file(file)
                .header("Authorization", generateTestToken()))
            .andExpect(status().isOk());
    }

    @Test
    void POST_api_chat_upload_지원하지_않는_형식이면_400을_반환한다() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "bad.exe", "application/octet-stream", "binary".getBytes()
        );
        doThrow(new IllegalArgumentException("지원하지 않는 파일 형식입니다: exe"))
            .when(documentService).ingest(any());

        mockMvc.perform(multipart("/api/chat/upload")
                .file(file)
                .header("Authorization", generateTestToken()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("지원하지 않는 파일 형식입니다: exe"));
    }

    @Test
    void POST_api_chat_upload_빈_파일이면_400을_반환한다() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "empty.txt", "text/plain", new byte[0]
        );
        doThrow(new IllegalArgumentException("빈 파일은 업로드할 수 없습니다."))
            .when(documentService).ingest(any());

        mockMvc.perform(multipart("/api/chat/upload")
                .file(file)
                .header("Authorization", generateTestToken()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("빈 파일은 업로드할 수 없습니다."));
    }

    @Test
    void GET_api_chat_search_결과_목록을_반환한다() throws Exception {
        when(chatbotService.search(anyString(), anyInt()))
            .thenReturn(List.of(new SearchResponse("검색 내용", Map.of("source", "a.pdf"))));

        mockMvc.perform(get("/api/chat/search")
                .header("Authorization", generateTestToken())
                .param("query", "서울")
                .param("k", "4"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].content").value("검색 내용"));
    }

    @Test
    void GET_api_chat_search_query가_공백이면_400을_반환한다() throws Exception {
        mockMvc.perform(get("/api/chat/search")
                .header("Authorization", generateTestToken())
                .param("query", "   ")
                .param("k", "4"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void GET_api_chat_search_k가_0이면_400을_반환한다() throws Exception {
        mockMvc.perform(get("/api/chat/search")
                .header("Authorization", generateTestToken())
                .param("query", "서울")
                .param("k", "0"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void GET_api_chat_search_k가_21이면_400을_반환한다() throws Exception {
        mockMvc.perform(get("/api/chat/search")
                .header("Authorization", generateTestToken())
                .param("query", "서울")
                .param("k", "21"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void Authorization_헤더_없이_chat_요청하면_401을_반환한다() throws Exception {
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ChatRequest("질문"))))
            .andExpect(status().isUnauthorized());
    }
}
