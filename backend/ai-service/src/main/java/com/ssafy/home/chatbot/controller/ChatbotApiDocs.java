package com.ssafy.home.chatbot.controller;

import com.ssafy.home.chatbot.dto.ChatRequest;
import com.ssafy.home.chatbot.dto.ChatResponse;
import com.ssafy.home.chatbot.dto.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "ChatBot", description = "RAG 기반 AI 챗봇 API")
public interface ChatbotApiDocs {

    @Operation(summary = "AI 채팅", description = "업로드된 문서를 참고한 RAG 기반 응답을 반환한다.")
    ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request);

    @Operation(summary = "문서 업로드", description = "txt·md·pdf 문서를 업로드하여 벡터 스토어에 저장한다.")
    ResponseEntity<Void> upload(@RequestParam("file") MultipartFile file) throws IOException;

    @Operation(summary = "벡터 검색", description = "질문과 유사한 문서 청크를 반환한다.")
    ResponseEntity<List<SearchResponse>> search(
        @RequestParam String query,
        @RequestParam(defaultValue = "4") int k
    );
}
