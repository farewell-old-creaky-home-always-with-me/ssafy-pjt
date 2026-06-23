package com.ssafy.home.toolcalling.service;

import com.ssafy.home.toolcalling.dto.ToolChatResponse;
import com.ssafy.home.toolcalling.dto.ToolTestResponse;
import com.ssafy.home.toolcalling.tool.HouseSearchTool;
import com.ssafy.home.toolcalling.tool.StatsTool;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ToolCallingService {

    private static final String TOOL_SYSTEM_PROMPT = """
        당신은 부동산 정보 도우미입니다.
        지역별 평균 거래가, 거래 통계, 가격 추이 질문은 getRegionRealEstateStats 도구를 사용하세요.
        지역, 유형, 금액 조건의 매물 추천이나 주택 목록 질문은 searchHousesByCondition 도구를 사용하세요.
        한 번의 요청에서는 필요한 단일 도구만 사용하고, 도구 결과를 바탕으로 간결하게 답변하세요.
        """;

    private final ChatClient chatClient;
    private final StatsTool statsTool;
    private final HouseSearchTool houseSearchTool;

    public ToolChatResponse chat(String message) {
        String answer = chatClient.prompt()
            .system(TOOL_SYSTEM_PROMPT)
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
