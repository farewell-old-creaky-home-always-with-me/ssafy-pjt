package com.ssafy.home.batchreport.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.home.batchreport.dto.BatchReportSummaryRequest;
import com.ssafy.home.batchreport.dto.HouseDealSummaryItem;
import java.math.BigDecimal;
import java.time.LocalDate;
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
class BatchReportSummaryServiceTest {

    @Mock
    private ChatModel chatModel;

    private BatchReportSummaryService service;

    @BeforeEach
    void setUp() {
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        service = new BatchReportSummaryService(chatClient, new ObjectMapper());
    }

    @Test
    void summarizeReturnsParsedAiResult() {
        when(chatModel.call(any(Prompt.class))).thenReturn(
                new org.springframework.ai.chat.model.ChatResponse(
                        List.of(new Generation(new AssistantMessage("""
                                {"summary":"강남구 거래 요약입니다.","translatedSummary":"This is a Gangnam transaction summary."}
                                """)))));

        var response = service.summarize(new BatchReportSummaryRequest(
                "11680",
                "202606",
                10L,
                1L,
                0,
                List.of(new HouseDealSummaryItem(
                        "역삼래미안",
                        "역삼동",
                        "APARTMENT",
                        "SALE",
                        120000,
                        LocalDate.of(2026, 6, 10),
                        BigDecimal.valueOf(84.5),
                        10
                ))
        ));

        assertThat(response.summary()).isEqualTo("강남구 거래 요약입니다.");
        assertThat(response.translatedSummary()).isEqualTo("This is a Gangnam transaction summary.");
    }

    @Test
    void summarizeParsesUppercaseCodeFenceLanguage() {
        when(chatModel.call(any(Prompt.class))).thenReturn(
                new org.springframework.ai.chat.model.ChatResponse(
                        List.of(new Generation(new AssistantMessage("""
                                ```JSON
                                {"summary":"요약입니다.","translatedSummary":"Summary."}
                                ```
                                """)))));

        var response = service.summarize(request());

        assertThat(response.summary()).isEqualTo("요약입니다.");
        assertThat(response.translatedSummary()).isEqualTo("Summary.");
    }

    @Test
    void summarizeParsesOtherCodeFenceLanguage() {
        when(chatModel.call(any(Prompt.class))).thenReturn(
                new org.springframework.ai.chat.model.ChatResponse(
                        List.of(new Generation(new AssistantMessage("""
                                ```javascript
                                {"summary":"요약입니다.","translatedSummary":"Summary."}
                                ```
                                """)))));

        var response = service.summarize(request());

        assertThat(response.summary()).isEqualTo("요약입니다.");
        assertThat(response.translatedSummary()).isEqualTo("Summary.");
    }

    @Test
    void summarizeRejectsInvalidAiResponse() {
        when(chatModel.call(any(Prompt.class))).thenReturn(
                new org.springframework.ai.chat.model.ChatResponse(
                        List.of(new Generation(new AssistantMessage("""
                                {"summary":"요약입니다."}
                                """)))));

        assertThatThrownBy(() -> service.summarize(request()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("summary and translatedSummary");
    }

    private BatchReportSummaryRequest request() {
        return new BatchReportSummaryRequest(
                "11680",
                "202606",
                10L,
                1L,
                0,
                List.of(new HouseDealSummaryItem(
                        "역삼래미안",
                        "역삼동",
                        "APARTMENT",
                        "SALE",
                        120000,
                        LocalDate.of(2026, 6, 10),
                        BigDecimal.valueOf(84.5),
                        10
                ))
        );
    }
}
