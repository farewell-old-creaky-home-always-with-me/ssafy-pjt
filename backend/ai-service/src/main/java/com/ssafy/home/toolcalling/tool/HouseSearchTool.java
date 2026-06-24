package com.ssafy.home.toolcalling.tool;

import com.ssafy.home.toolcalling.support.RealEstateToolDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HouseSearchTool {

    private final RealEstateToolDataProvider dataProvider;

    @Tool(
        name = "searchHousesByCondition",
        description = "지역, 주택 유형, 최대 금액 조건으로 주택 목록을 조회합니다. 추천 매물이나 조건 검색 질문에 사용합니다."
    )
    public String searchHouses(
        @ToolParam(description = "지역 이름. 예: 강남구, 서울", required = false) String regionName,
        @ToolParam(description = "주택 유형. 예: 아파트, 다세대", required = false) String houseType,
        @ToolParam(description = "최대 금액. 단위는 만원입니다. 예: 50000", required = false) Integer maxPrice
    ) {
        return dataProvider.searchHouses(regionName, houseType, maxPrice);
    }
}
