package com.ssafy.home.toolcalling.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ssafy.home.toolcalling.planner.ToolCallPlanner;
import com.ssafy.home.toolcalling.prompt.ToolCallingPromptProvider;
import com.ssafy.home.toolcalling.tool.HouseSearchTool;
import com.ssafy.home.toolcalling.tool.StatsTool;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;

@ExtendWith(MockitoExtension.class)
class ToolCallingServicePlannerTest {

    @Mock
    private ChatModel chatModel;

    @Mock
    private StatsTool statsTool;

    @Mock
    private HouseSearchTool houseSearchTool;

    private ToolCallingService toolCallingService;

    @BeforeEach
    void setUp() {
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        toolCallingService = new ToolCallingService(
            chatClient,
            new ToolCallingPromptProvider(),
            statsTool,
            houseSearchTool,
            new ToolCallPlanner()
        );
    }

    @Test
    @DisplayName("매물 추천만 필요한 질문은 통계 도구를 호출하지 않는다")
    void multiChatSkipsStatsToolWhenOnlyHouseSearchIsNeeded() {
        given(houseSearchTool.searchHouses("송파구", "아파트", null))
            .willReturn("[HouseSearchTool 결과] 검색 지역: 송파구, 주택 유형: 아파트");
        given(chatModel.call(any(Prompt.class))).willReturn(
            new org.springframework.ai.chat.model.ChatResponse(
                List.of(new Generation(new AssistantMessage("송파구 아파트 추천입니다.")))));

        var response = toolCallingService.multiChat("송파구 아파트 추천해줘");

        assertThat(response.answer()).isEqualTo("송파구 아파트 추천입니다.");
        assertThat(response.statsResult()).isEmpty();
        assertThat(response.houseSearchResult()).contains("송파구");
        assertThat(response.steps()).hasSize(1);
        assertThat(response.steps().get(0).toolName()).isEqualTo("searchHousesByCondition");
        then(statsTool).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("통계만 필요한 질문은 매물 검색 도구를 호출하지 않는다")
    void multiChatSkipsHouseSearchToolWhenOnlyStatsIsNeeded() {
        given(statsTool.getRegionStats("강남구", "평균 거래가", "최근 3개월"))
            .willReturn("[StatsTool 결과] 지역: 강남구, 평균 거래가: 12억 4,000만원");
        given(chatModel.call(any(Prompt.class))).willReturn(
            new org.springframework.ai.chat.model.ChatResponse(
                List.of(new Generation(new AssistantMessage("강남구 평균 거래가입니다.")))));

        var response = toolCallingService.multiChat("강남구 평균 거래가는?");

        assertThat(response.answer()).isEqualTo("강남구 평균 거래가입니다.");
        assertThat(response.statsResult()).contains("강남구");
        assertThat(response.houseSearchResult()).isEmpty();
        assertThat(response.steps()).hasSize(1);
        assertThat(response.steps().get(0).toolName()).isEqualTo("getRegionRealEstateStats");
        then(houseSearchTool).shouldHaveNoInteractions();
    }
}
