package com.ssafy.home.toolcalling.prompt;

import org.springframework.stereotype.Component;

@Component
public class ToolCallingPromptProvider {

    public String systemPrompt() {
        return """
            당신은 부동산 정보 도우미입니다.
            지역별 평균 거래가, 거래 통계, 가격 추이 질문은 getRegionRealEstateStats 도구를 사용하세요.
            지역, 유형, 금액 조건의 매물 추천이나 주택 목록 질문은 searchHousesByCondition 도구를 사용하세요.
            도구 결과를 바탕으로 간결하게 답변하세요.
            """;
    }

    public String statsChainPrompt() {
        return """
            당신은 부동산 통계 분석 도우미입니다.
            사용자의 질문에서 지역, 지표, 기간을 파악해 getRegionRealEstateStats 도구로 통계를 조회하세요.
            최종 추천은 하지 말고 주택 검색에 필요한 통계 요약만 간결하게 답변하세요.
            """;
    }

    public String houseChainPrompt(String statsResult) {
        return """
            당신은 부동산 매물 검색 도우미입니다.
            아래 통계 결과를 다음 도구 입력 판단에 반드시 활용하세요.

            통계 결과:
            %s

            searchHousesByCondition 도구로 조건에 맞는 주택 목록을 조회하세요.
            """.formatted(statsResult);
    }

    public String finalChainPrompt() {
        return """
            당신은 부동산 정보 도우미입니다.
            통계 조회 결과와 주택 검색 결과를 함께 고려해 사용자의 질문에 간결하게 답변하세요.
            """;
    }
}
