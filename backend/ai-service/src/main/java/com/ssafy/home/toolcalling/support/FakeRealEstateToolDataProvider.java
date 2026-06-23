package com.ssafy.home.toolcalling.support;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class FakeRealEstateToolDataProvider implements RealEstateToolDataProvider {

    @Override
    public String getRegionStats(String regionName, String metric, String period) {
        String region = StringUtils.hasText(regionName) ? regionName : "강남구";
        String normalizedMetric = StringUtils.hasText(metric) ? metric : "평균 거래가";
        String normalizedPeriod = StringUtils.hasText(period) ? period : "최근 3개월";

        return """
            [StatsTool 결과]
            지역: %s
            지표: %s
            기간: %s
            평균 거래가: 12억 4,000만원
            거래량 추이: 전월 대비 8%% 증가
            """.formatted(region, normalizedMetric, normalizedPeriod).trim();
    }

    @Override
    public String searchHouses(String regionName, String houseType, Integer maxPrice) {
        String region = StringUtils.hasText(regionName) ? regionName : "서울";
        String type = StringUtils.hasText(houseType) ? houseType : "아파트";
        int priceLimit = maxPrice != null ? maxPrice : 50_000;

        return """
            [HouseSearchTool 결과]
            검색 지역: %s
            주택 유형: %s
            최대 금액: %,d만원
            추천 매물:
            1. 역삼래미안 59㎡, 48,000만원
            2. 대치푸르지오 45㎡, 50,000만원
            """.formatted(region, type, priceLimit).trim();
    }
}
