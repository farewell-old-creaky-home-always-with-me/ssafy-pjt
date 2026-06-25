package com.ssafy.home.chatbot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ssafy.home.chatbot.dto.ChatResponse;
import com.ssafy.home.chatbot.dto.SearchResponse;
import com.ssafy.home.chatbot.prompt.ChatbotPromptProvider;
import java.util.List;
import java.util.Map;
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
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

@ExtendWith(MockitoExtension.class)
class ChatbotServiceTest {

    @Mock
    private VectorStore vectorStore;
    @Mock
    private ChatModel chatModel;

    private ChatbotService chatbotService;

    @BeforeEach
    void setUp() {
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        chatbotService = new ChatbotService(chatClient, vectorStore, new ChatbotPromptProvider());
    }

    @Test
    void 관련_문서가_있으면_ragUsed가_true다() {
        Document doc = new Document("서울 아파트 평균 가격은 10억입니다.", Map.of("source", "test.txt"));
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
            .thenReturn(List.of(doc));
        when(chatModel.call(any(Prompt.class))).thenReturn(
            new org.springframework.ai.chat.model.ChatResponse(
                List.of(new Generation(new AssistantMessage("AI 응답")))));

        ChatResponse response = chatbotService.chat("서울 아파트 가격");

        assertThat(response.ragUsed()).isTrue();
        assertThat(response.answer()).isEqualTo("AI 응답");
    }

    @Test
    void 관련_문서가_있으면_분리된_prompt_provider의_system_prompt를_사용한다() {
        Document doc = new Document("서울 아파트 평균 가격은 10억입니다.", Map.of("source", "test.txt"));
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
            .thenReturn(List.of(doc));
        when(chatModel.call(any(Prompt.class))).thenReturn(
            new org.springframework.ai.chat.model.ChatResponse(
                List.of(new Generation(new AssistantMessage("AI 응답")))));

        chatbotService.chat("서울 아파트 가격");

        ArgumentCaptor<Prompt> promptCaptor = ArgumentCaptor.forClass(Prompt.class);
        verify(chatModel).call(promptCaptor.capture());
        assertThat(promptCaptor.getValue().getInstructions())
            .anySatisfy(message -> assertThat(message.getText()).contains("참고 문서"));
        assertThat(promptCaptor.getValue().getInstructions())
            .anySatisfy(message -> assertThat(message.getText()).contains("서울 아파트 평균 가격은 10억입니다."));
    }

    @Test
    void 관련_문서가_없으면_ragUsed가_false다() {
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
            .thenReturn(List.of());
        when(chatModel.call(any(Prompt.class))).thenReturn(
            new org.springframework.ai.chat.model.ChatResponse(
                List.of(new Generation(new AssistantMessage("일반 AI 응답")))));

        ChatResponse response = chatbotService.chat("날씨");

        assertThat(response.ragUsed()).isFalse();
        assertThat(response.answer()).isEqualTo("일반 AI 응답");
    }

    @Test
    void search는_유사_문서_목록을_반환한다() {
        Document doc = new Document("테스트 내용", Map.of("source", "doc.pdf"));
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
            .thenReturn(List.of(doc));

        List<SearchResponse> results = chatbotService.search("테스트", 4);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).content()).isEqualTo("테스트 내용");
        assertThat(results.get(0).metadata()).containsEntry("source", "doc.pdf");
    }
}
