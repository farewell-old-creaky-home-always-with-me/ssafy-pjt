package com.ssafy.home.toolcalling.dto;

import jakarta.validation.constraints.NotBlank;

public record ToolMultiChatRequest(
    @NotBlank String message
) {
}
