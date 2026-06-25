package com.ssafy.home.toolcalling.planner;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ToolCallPlannerTest {

    private final ToolCallPlanner planner = new ToolCallPlanner();

    @Test
    @DisplayName("평균 거래가와 추천을 함께 요청하면 통계 조회 후 매물 검색 계획을 만든다")
    void planStatsThenHouseSearch() {
        ToolCallPlan plan = planner.plan("강남구 평균 거래가를 기준으로 아파트 추천해줘");

        assertThat(plan.steps())
            .containsExactly(ToolCallStep.REGION_STATS, ToolCallStep.HOUSE_SEARCH);
    }

    @Test
    @DisplayName("매물 추천만 요청하면 매물 검색만 계획한다")
    void planOnlyHouseSearch() {
        ToolCallPlan plan = planner.plan("송파구 아파트 추천해줘");

        assertThat(plan.steps())
            .containsExactly(ToolCallStep.HOUSE_SEARCH);
    }

    @Test
    @DisplayName("평균 거래가만 질문하면 통계 조회만 계획한다")
    void planOnlyStats() {
        ToolCallPlan plan = planner.plan("강남구 평균 거래가는?");

        assertThat(plan.steps())
            .containsExactly(ToolCallStep.REGION_STATS);
    }

    @Test
    @DisplayName("부동산 도구가 필요 없는 질문이면 도구 호출 계획을 만들지 않는다")
    void planNoTool() {
        ToolCallPlan plan = planner.plan("안녕?");

        assertThat(plan.steps()).isEmpty();
    }
}
