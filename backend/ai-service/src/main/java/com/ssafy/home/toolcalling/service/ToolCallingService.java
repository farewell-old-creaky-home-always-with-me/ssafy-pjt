package com.ssafy.home.toolcalling.service;

import com.ssafy.home.toolcalling.dto.ToolChatResponse;
import com.ssafy.home.toolcalling.dto.ToolMultiChatResponse;
import com.ssafy.home.toolcalling.dto.ToolTestResponse;
import com.ssafy.home.toolcalling.prompt.ToolCallingPromptProvider;
import com.ssafy.home.toolcalling.tool.HouseSearchTool;
import com.ssafy.home.toolcalling.tool.StatsTool;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ToolCallingService {

    private final ChatClient chatClient;
    private final ToolCallingPromptProvider promptProvider;
    private final StatsTool statsTool;
    private final HouseSearchTool houseSearchTool;

    public ToolChatResponse chat(String message) {
        String answer = chatClient.prompt()
            .system(promptProvider.systemPrompt())
            .tools(statsTool, houseSearchTool)
            .user(message)
            .call()
            .content();
        return new ToolChatResponse(answer, true);
    }

    public ToolMultiChatResponse multiChat(String message) {
        String statsResult = chatClient.prompt()
            .system(promptProvider.statsChainPrompt())
            .tools(statsTool)
            .user(message)
            .call()
            .content();

        String houseSearchResult = chatClient.prompt()
            .system(promptProvider.houseChainPrompt(statsResult))
            .tools(houseSearchTool)
            .user(message)
            .call()
            .content();

        String answer = chatClient.prompt()
            .system(promptProvider.finalChainPrompt())
            .user("""
                사용자 질문:
                %s

                통계 결과:
                %s

                주택 검색 결과:
                %s
                """.formatted(message, statsResult, houseSearchResult))
            .call()
            .content();

        return new ToolMultiChatResponse(answer, statsResult, houseSearchResult, true);
    }

    public ToolTestResponse testTools() {
        return new ToolTestResponse(
            statsTool.getRegionStats("강남구", "평균 거래가", "최근 3개월"),
            houseSearchTool.searchHouses("강남구", "아파트", 50_000)
        );
    }
}
