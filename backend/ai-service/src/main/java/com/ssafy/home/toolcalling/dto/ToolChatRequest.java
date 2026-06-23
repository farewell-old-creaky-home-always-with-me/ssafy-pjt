package com.ssafy.home.toolcalling.dto;

import jakarta.validation.constraints.NotBlank;

public record ToolChatRequest(
    @NotBlank String message
) {
}
