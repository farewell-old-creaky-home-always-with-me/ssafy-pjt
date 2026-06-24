package com.ssafy.home.batchreport.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.home.batchreport.dto.BatchReportSummaryRequest;
import com.ssafy.home.batchreport.dto.BatchReportSummaryResponse;
import com.ssafy.home.batchreport.dto.HouseDealSummaryItem;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class BatchReportSummaryService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public BatchReportSummaryResponse summarize(BatchReportSummaryRequest request) {
        String content = chatClient.prompt()
                .system("""
                        You are a real-estate batch report assistant.
                        Return only strict JSON with keys summary and translatedSummary.
                        summary must be Korean.
                        translatedSummary must be English.
                        Include collection outcome, notable transaction samples, data quality impact, and operational improvement points.
                        """)
                .user(prompt(request))
                .call()
                .content();
        return parse(content);
    }

    private String prompt(BatchReportSummaryRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append("Create an AI summary/translation report from this batch collection result.\n");
        builder.append("regionCode=").append(value(request.regionCode())).append('\n');
        builder.append("yearMonth=").append(value(request.yearMonth())).append('\n');
        builder.append("collectedCount=").append(value(request.collectedCount())).append('\n');
        builder.append("skippedCount=").append(value(request.skippedCount())).append('\n');
        builder.append("failedCount=").append(value(request.failedCount())).append('\n');
        builder.append("Sample house deals:\n");

        List<HouseDealSummaryItem> deals = request.deals() == null ? List.of() : request.deals();
        for (HouseDealSummaryItem deal : deals) {
            builder.append("- apt=").append(value(deal.aptName()))
                    .append(", dong=").append(value(deal.dongName()))
                    .append(", type=").append(value(deal.houseType()))
                    .append(", dealType=").append(value(deal.dealType()))
                    .append(", amount=").append(value(deal.dealAmount()))
                    .append(", date=").append(value(deal.dealDate()))
                    .append(", area=").append(value(deal.area()))
                    .append(", floor=").append(value(deal.floor()))
                    .append('\n');
        }
        return builder.toString();
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

    private Object value(Object value) {
        return value == null ? "" : value;
    }
}
