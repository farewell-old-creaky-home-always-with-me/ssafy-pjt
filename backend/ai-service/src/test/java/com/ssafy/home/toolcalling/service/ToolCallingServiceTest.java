package com.ssafy.home.toolcalling.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ssafy.home.toolcalling.prompt.ToolCallingPromptProvider;
import com.ssafy.home.toolcalling.support.FakeRealEstateToolDataProvider;
import com.ssafy.home.toolcalling.tool.HouseSearchTool;
import com.ssafy.home.toolcalling.tool.StatsTool;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;

@ExtendWith(MockitoExtension.class)
class ToolCallingServiceTest {

    @Mock
    private ChatModel chatModel;

    private ToolCallingService toolCallingService;

    @BeforeEach
    void setUp() {
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        FakeRealEstateToolDataProvider dataProvider = new FakeRealEstateToolDataProvider();
        toolCallingService = new ToolCallingService(
            chatClient,
            new ToolCallingPromptProvider(),
            new StatsTool(dataProvider),
            new HouseSearchTool(dataProvider)
        );
    }

    @Test
    void tool_chat은_tool_calling_활성화_응답을_반환한다() {
        given(chatModel.call(any(Prompt.class))).willReturn(
            new org.springframework.ai.chat.model.ChatResponse(
                List.of(new Generation(new AssistantMessage("AI 응답입니다.")))));

        var response = toolCallingService.chat("강남구 평균 거래가는?");

        assertThat(response.toolCallingEnabled()).isTrue();
        assertThat(response.answer()).isEqualTo("AI 응답입니다.");
    }

    @Test
    void tool_chat은_분리된_prompt_provider의_system_prompt를_사용한다() {
        given(chatModel.call(any(Prompt.class))).willReturn(
            new org.springframework.ai.chat.model.ChatResponse(
                List.of(new Generation(new AssistantMessage("AI 응답입니다.")))));

        toolCallingService.chat("강남구 평균 거래가는?");

        ArgumentCaptor<Prompt> promptCaptor = ArgumentCaptor.forClass(Prompt.class);
        then(chatModel).should().call(promptCaptor.capture());
        assertThat(promptCaptor.getValue().getInstructions())
            .anySatisfy(message -> assertThat(message.getText()).contains("getRegionRealEstateStats"));
        assertThat(promptCaptor.getValue().getInstructions())
            .noneSatisfy(message -> assertThat(message.getText()).contains("단일 도구만 사용"));
    }

    @Test
    void multiChat은_첫_tool_결과를_다음_tool_입력으로_사용한다() {
        given(chatModel.call(any(Prompt.class))).willReturn(
            new org.springframework.ai.chat.model.ChatResponse(
                List.of(new Generation(new AssistantMessage("통계 기반 추천 응답입니다.")))));

        var response = toolCallingService.multiChat("강남구 평균 거래가를 기준으로 아파트 추천해줘");

        assertThat(response.toolCallingEnabled()).isTrue();
        assertThat(response.toolChainEnabled()).isTrue();
        assertThat(response.answer()).isEqualTo("통계 기반 추천 응답입니다.");
        assertThat(response.statsResult()).contains("[StatsTool 결과]", "지역: 강남구");
        assertThat(response.houseSearchResult()).contains("[HouseSearchTool 결과]", "검색 지역: 강남구", "최대 금액: 124,000만원");
        assertThat(response.steps()).hasSize(2);
        assertThat(response.steps().get(0).toolName()).isEqualTo("getRegionRealEstateStats");
        assertThat(response.steps().get(0).result()).contains("[StatsTool 결과]", "지역: 강남구");
        assertThat(response.steps().get(1).toolName()).isEqualTo("searchHousesByCondition");
        assertThat(response.steps().get(1).input())
            .contains("getRegionRealEstateStats 결과 기반", "regionName=강남구", "maxPrice=124000");
        assertThat(response.steps().get(1).result())
            .contains("[HouseSearchTool 결과]", "검색 지역: 강남구", "최대 금액: 124,000만원");
    }

    @Test
    void testTools는_두_tool의_직접_실행_결과를_반환한다() {
        var response = toolCallingService.testTools();

        assertThat(response.statsResult()).contains("[StatsTool 결과]", "강남구");
        assertThat(response.houseSearchResult()).contains("[HouseSearchTool 결과]", "아파트");
    }
}
