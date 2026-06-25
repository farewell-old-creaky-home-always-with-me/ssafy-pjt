package com.ssafy.home.toolcalling.service;

import com.ssafy.home.toolcalling.dto.ToolChatResponse;
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

    public ToolTestResponse testTools() {
        return new ToolTestResponse(
            statsTool.getRegionStats("강남구", "평균 거래가", "최근 3개월"),
            houseSearchTool.searchHouses("강남구", "아파트", 50_000)
        );
    }
}
