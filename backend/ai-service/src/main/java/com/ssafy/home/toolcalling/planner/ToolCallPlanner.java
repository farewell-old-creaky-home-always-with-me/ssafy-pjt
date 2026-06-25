package com.ssafy.home.toolcalling.planner;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ToolCallPlanner {

    public ToolCallPlan plan(String message) {
        boolean needsStats = containsAny(message, "평균", "거래가", "거래량", "통계", "추이");
        boolean needsHouseSearch = containsAny(message, "추천", "매물", "목록", "검색");

        List<ToolCallStep> steps = new ArrayList<>();
        if (needsStats) {
            steps.add(ToolCallStep.REGION_STATS);
        }
        if (needsHouseSearch) {
            steps.add(ToolCallStep.HOUSE_SEARCH);
        }

        return new ToolCallPlan(steps);
    }

    private boolean containsAny(String message, String... keywords) {
        if (message == null) {
            return false;
        }
        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
