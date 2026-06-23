package com.ssafy.home.toolcalling.tool;

import com.ssafy.home.toolcalling.support.RealEstateToolDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatsTool {

    private final RealEstateToolDataProvider dataProvider;

    @Tool(
        name = "getRegionRealEstateStats",
        description = "지역별 부동산 거래 통계, 평균 거래가, 거래량 추이를 조회합니다. 평균 거래가나 추이를 묻는 질문에 사용합니다."
    )
    public String getRegionStats(
        @ToolParam(description = "지역 이름. 예: 강남구, 송파구, 서울") String regionName,
        @ToolParam(description = "조회 지표. 예: 평균 거래가, 거래량, 가격 추이", required = false) String metric,
        @ToolParam(description = "조회 기간. 예: 최근 3개월, 2026년 상반기", required = false) String period
    ) {
        return dataProvider.getRegionStats(regionName, metric, period);
    }
}
