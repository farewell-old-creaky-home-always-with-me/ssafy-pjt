package com.ssafy.home.chatbot.dto;

import java.util.Map;

public record SearchResponse(
    String content,
    Map<String, Object> metadata
) {}
