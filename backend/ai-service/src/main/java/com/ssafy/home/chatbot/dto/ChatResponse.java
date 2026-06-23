package com.ssafy.home.chatbot.dto;

public record ChatResponse(
    String answer,
    boolean ragUsed
) {}
