package com.ssafy.home.toolcalling.controller;

import com.ssafy.home.global.interceptor.LoginRequired;
import com.ssafy.home.toolcalling.dto.ToolChatRequest;
import com.ssafy.home.toolcalling.dto.ToolChatResponse;
import com.ssafy.home.toolcalling.dto.ToolTestResponse;
import com.ssafy.home.toolcalling.service.ToolCallingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LoginRequired
@RestController
@RequestMapping("/api/ai/tools")
@RequiredArgsConstructor
public class ToolCallingController {

    private final ToolCallingService toolCallingService;

    @PostMapping("/chat")
    public ResponseEntity<ToolChatResponse> chat(@Valid @RequestBody ToolChatRequest request) {
        return ResponseEntity.ok(toolCallingService.chat(request.message()));
    }

    @GetMapping("/test")
    public ResponseEntity<ToolTestResponse> test() {
        return ResponseEntity.ok(toolCallingService.testTools());
    }
}
