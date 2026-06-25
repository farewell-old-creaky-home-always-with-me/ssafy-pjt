package com.ssafy.home.toolcalling.dto;

public record ToolMultiChatResponse(
    String answer,
    String statsResult,
    String houseSearchResult,
    boolean toolChainEnabled
) {
}
