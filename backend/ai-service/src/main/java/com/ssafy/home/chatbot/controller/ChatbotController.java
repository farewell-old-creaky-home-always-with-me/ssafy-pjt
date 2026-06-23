package com.ssafy.home.chatbot.controller;

import com.ssafy.home.chatbot.dto.ChatRequest;
import com.ssafy.home.chatbot.dto.ChatResponse;
import com.ssafy.home.chatbot.dto.SearchResponse;
import com.ssafy.home.chatbot.service.ChatbotService;
import com.ssafy.home.chatbot.service.DocumentService;
import com.ssafy.home.global.interceptor.LoginRequired;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@LoginRequired
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatbotController implements ChatbotApiDocs {

    private final ChatbotService chatbotService;
    private final DocumentService documentService;

    @PostMapping
    @Override
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatbotService.chat(request.message()));
    }

    @PostMapping("/upload")
    @Override
    public ResponseEntity<Void> upload(@RequestParam("file") MultipartFile file) throws IOException {
        documentService.ingest(file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @Override
    public ResponseEntity<List<SearchResponse>> search(
        @RequestParam String query,
        @RequestParam(defaultValue = "4") int k
    ) {
        return ResponseEntity.ok(chatbotService.search(query, k));
    }
}
