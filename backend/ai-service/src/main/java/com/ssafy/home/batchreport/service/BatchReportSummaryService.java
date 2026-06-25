package com.ssafy.home.batchreport.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.home.batchreport.dto.BatchReportSummaryRequest;
import com.ssafy.home.batchreport.dto.BatchReportSummaryResponse;
import com.ssafy.home.batchreport.prompt.BatchReportSummaryPromptProvider;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class BatchReportSummaryService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final BatchReportSummaryPromptProvider promptProvider;

    public BatchReportSummaryResponse summarize(BatchReportSummaryRequest request) {
        String content = chatClient.prompt()
                .system(promptProvider.systemPrompt())
                .user(promptProvider.userPrompt(request))
                .call()
                .content();
        return parse(content);
    }

    private BatchReportSummaryResponse parse(String content) {
        try {
            JsonNode root = objectMapper.readTree(stripCodeFence(content));
            String summary = root.path("summary").asText();
            String translatedSummary = root.path("translatedSummary").asText();
            if (!StringUtils.hasText(summary) || !StringUtils.hasText(translatedSummary)) {
                throw new IllegalStateException("AI response must contain summary and translatedSummary");
            }
            return new BatchReportSummaryResponse(summary, translatedSummary);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to parse AI summary response", exception);
        }
    }

    private String stripCodeFence(String content) {
        if (content == null) {
            return "";
        }
        String trimmed = content.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replaceFirst("(?i)^```[a-z0-9_-]*\\s*", "")
                    .replaceFirst("\\s*```$", "");
        }
        return trimmed;
    }

}
