package com.ssafy.home.toolcalling.prompt;

import org.springframework.stereotype.Component;

@Component
public class ToolCallingPromptProvider {

    public String systemPrompt() {
        return """
            당신은 부동산 정보 도우미입니다.
            지역별 평균 거래가, 거래 통계, 가격 추이 질문은 getRegionRealEstateStats 도구를 사용하세요.
            지역, 유형, 금액 조건의 매물 추천이나 주택 목록 질문은 searchHousesByCondition 도구를 사용하세요.
            한 번의 요청에서는 필요한 단일 도구만 사용하고, 도구 결과를 바탕으로 간결하게 답변하세요.
            """;
    }
}
