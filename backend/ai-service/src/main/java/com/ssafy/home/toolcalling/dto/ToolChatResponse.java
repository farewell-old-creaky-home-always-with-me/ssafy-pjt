package com.ssafy.home.toolcalling.dto;

public record ToolChatResponse(
    String answer,
    boolean toolCallingEnabled
) {
}
