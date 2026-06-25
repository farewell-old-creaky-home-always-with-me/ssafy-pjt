package com.ssafy.home.toolcalling.dto;

public record ToolMultiStepResponse(
    String toolName,
    String input,
    String result
) {
}
