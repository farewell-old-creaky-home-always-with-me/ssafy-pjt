package com.ssafy.home.toolcalling.dto;

import java.util.List;

public record ToolMultiChatResponse(
    String answer,
    String statsResult,
    String houseSearchResult,
    boolean toolChainEnabled,
    boolean toolCallingEnabled,
    List<ToolMultiStepResponse> steps
) {

    public ToolMultiChatResponse(String answer, String statsResult, String houseSearchResult, boolean toolChainEnabled) {
        this(
            answer,
            statsResult,
            houseSearchResult,
            toolChainEnabled,
            toolChainEnabled,
            List.of(
                new ToolMultiStepResponse("getRegionRealEstateStats", "statsResult", statsResult),
                new ToolMultiStepResponse("searchHousesByCondition", "houseSearchResult", houseSearchResult)
            )
        );
    }
}
