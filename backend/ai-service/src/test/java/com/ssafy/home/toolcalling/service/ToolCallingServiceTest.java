package com.ssafy.home.toolcalling.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ssafy.home.toolcalling.support.FakeRealEstateToolDataProvider;
import com.ssafy.home.toolcalling.tool.HouseSearchTool;
import com.ssafy.home.toolcalling.tool.StatsTool;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
        when(chatModel.call(any(Prompt.class))).thenReturn(
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
}
