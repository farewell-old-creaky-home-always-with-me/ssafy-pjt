package com.ssafy.home.toolcalling.prompt;

import org.springframework.stereotype.Component;

@Component
public class ToolCallingPromptProvider {

    public String systemPrompt() {
        return """
            당신은 부동산 정보 도우미입니다.
            지역별 평균 거래가, 거래 통계, 가격 추이 질문은 getRegionRealEstateStats 도구를 사용하세요.
            지역, 유형, 금액 조건의 매물 추천이나 주택 목록 질문은 searchHousesByCondition 도구를 사용하세요.
            복합 질문은 필요한 도구를 순서대로 사용하고, 도구 결과를 바탕으로 간결하게 답변하세요.
            """;
    }

    public String multiToolSystemPrompt() {
        return """
            당신은 부동산 정보 도우미입니다.
            제공된 두 도구의 연쇄 실행 결과를 종합해서 간결하게 답변하세요.
            이전 도구 결과가 다음 도구 입력에 어떻게 반영됐는지 자연스럽게 설명하세요.
            """;
    }
}
