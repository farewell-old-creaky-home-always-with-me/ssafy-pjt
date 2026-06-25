package com.ssafy.home.toolcalling.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

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
    void testTools는_두_tool의_직접_실행_결과를_반환한다() {
        var response = toolCallingService.testTools();

        assertThat(response.statsResult()).contains("[StatsTool 결과]", "강남구");
        assertThat(response.houseSearchResult()).contains("[HouseSearchTool 결과]", "아파트");
    }

    @Test
    void multiChat은_통계_결과를_주택_검색_입력으로_연결한다() {
        given(chatModel.call(any(Prompt.class)))
            .willReturn(new org.springframework.ai.chat.model.ChatResponse(
                List.of(new Generation(new AssistantMessage("평균 거래가: 12억 4,000만원")))))
            .willReturn(new org.springframework.ai.chat.model.ChatResponse(
                List.of(new Generation(new AssistantMessage("추천 매물 목록")))))
            .willReturn(new org.springframework.ai.chat.model.ChatResponse(
                List.of(new Generation(new AssistantMessage("통계 기반 추천입니다.")))));

        var response = toolCallingService.multiChat("강남구 평균 거래가를 보고 예산에 맞는 아파트 추천해줘");

        ArgumentCaptor<Prompt> promptCaptor = ArgumentCaptor.forClass(Prompt.class);
        then(chatModel).should(times(3)).call(promptCaptor.capture());
        Prompt houseSearchPrompt = promptCaptor.getAllValues().get(1);

        assertThat(houseSearchPrompt.getContents()).contains("평균 거래가: 12억 4,000만원");
        assertThat(response.statsResult()).isEqualTo("평균 거래가: 12억 4,000만원");
        assertThat(response.houseSearchResult()).isEqualTo("추천 매물 목록");
        assertThat(response.answer()).isEqualTo("통계 기반 추천입니다.");
        assertThat(response.toolChainEnabled()).isTrue();
    }
}
