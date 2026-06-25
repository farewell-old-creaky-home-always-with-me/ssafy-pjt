package com.ssafy.home.toolcalling.service;

import com.ssafy.home.toolcalling.dto.ToolChatResponse;
import com.ssafy.home.toolcalling.dto.ToolMultiChatResponse;
import com.ssafy.home.toolcalling.dto.ToolMultiStepResponse;
import com.ssafy.home.toolcalling.dto.ToolTestResponse;
import com.ssafy.home.toolcalling.planner.ToolCallPlan;
import com.ssafy.home.toolcalling.planner.ToolCallPlanner;
import com.ssafy.home.toolcalling.planner.ToolCallStep;
import com.ssafy.home.toolcalling.prompt.ToolCallingPromptProvider;
import com.ssafy.home.toolcalling.tool.HouseSearchTool;
import com.ssafy.home.toolcalling.tool.StatsTool;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ToolCallingService {

    private static final Pattern REGION_PATTERN = Pattern.compile("(서울|[가-힣]+구)");
    private static final Pattern AVERAGE_PRICE_PATTERN = Pattern.compile("평균 거래가:\\s*([0-9,]+)억\\s*([0-9,]+)만원");

    private final ChatClient chatClient;
    private final ToolCallingPromptProvider promptProvider;
    private final StatsTool statsTool;
    private final HouseSearchTool houseSearchTool;
    private final ToolCallPlanner toolCallPlanner;

    public ToolChatResponse chat(String message) {
        String answer = chatClient.prompt()
            .system(promptProvider.systemPrompt())
            .tools(statsTool, houseSearchTool)
            .user(message)
            .call()
            .content();
        return new ToolChatResponse(answer, true);
    }

    public ToolMultiChatResponse multiChat(String message) {
        ToolCallPlan plan = toolCallPlanner.plan(message);
        String regionName = extractRegionName(message);
        String houseType = extractHouseType(message);
        String statsInput = "";
        String statsResult = "";
        String houseSearchInput = "";
        String houseSearchResult = "";
        List<ToolMultiStepResponse> steps = new ArrayList<>();

        if (plan.hasStep(ToolCallStep.REGION_STATS)) {
            statsInput = "regionName=%s, metric=평균 거래가, period=최근 3개월".formatted(regionName);
            statsResult = statsTool.getRegionStats(regionName, "평균 거래가", "최근 3개월");
            steps.add(new ToolMultiStepResponse("getRegionRealEstateStats", statsInput, statsResult));
        }

        if (plan.hasStep(ToolCallStep.HOUSE_SEARCH)) {
            String nextRegionName = statsResult.isBlank() ? regionName : extractRegionName(statsResult);
            Integer maxPrice = statsResult.isBlank() ? null : extractAveragePriceInManwon(statsResult);
            houseSearchInput = formatHouseSearchInput(statsResult, nextRegionName, houseType, maxPrice);
            houseSearchResult = houseSearchTool.searchHouses(nextRegionName, houseType, maxPrice);
            steps.add(new ToolMultiStepResponse("searchHousesByCondition", houseSearchInput, houseSearchResult));
        }

        String answer = chatClient.prompt()
            .system(promptProvider.multiToolSystemPrompt())
            .user("""
                사용자 질문:
                %s

                1단계 도구 입력:
                %s

                1단계 도구 결과:
                %s

                2단계 도구 입력:
                %s

                2단계 도구 결과:
                %s
                """.formatted(message, statsInput, statsResult, houseSearchInput, houseSearchResult))
            .call()
            .content();

        return new ToolMultiChatResponse(
            answer,
            statsResult,
            houseSearchResult,
            steps.size() > 1,
            plan.usesTool(),
            steps
        );
    }

    private String formatHouseSearchInput(String statsResult, String regionName, String houseType, Integer maxPrice) {
        if (statsResult.isBlank()) {
            return "regionName=%s, houseType=%s, maxPrice=%s".formatted(regionName, houseType, maxPrice);
        }
        return "getRegionRealEstateStats 결과 기반: regionName=%s, houseType=%s, maxPrice=%d"
            .formatted(regionName, houseType, maxPrice);
    }

    private String extractRegionName(String text) {
        Matcher matcher = REGION_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "강남구";
    }

    private String extractHouseType(String message) {
        if (message.contains("다세대")) {
            return "다세대";
        }
        if (message.contains("오피스텔")) {
            return "오피스텔";
        }
        return "아파트";
    }

    private int extractAveragePriceInManwon(String statsResult) {
        Matcher matcher = AVERAGE_PRICE_PATTERN.matcher(statsResult);
        if (matcher.find()) {
            int eok = Integer.parseInt(matcher.group(1).replace(",", ""));
            int manwon = Integer.parseInt(matcher.group(2).replace(",", ""));
            return eok * 10_000 + manwon;
        }
        return 50_000;
    }

    public ToolTestResponse testTools() {
        return new ToolTestResponse(
            statsTool.getRegionStats("강남구", "평균 거래가", "최근 3개월"),
            houseSearchTool.searchHouses("강남구", "아파트", 50_000)
        );
    }
}
