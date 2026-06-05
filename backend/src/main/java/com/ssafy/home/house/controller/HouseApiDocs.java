package com.ssafy.home.house.controller;

import com.ssafy.home.global.response.PageResponse;
import com.ssafy.home.house.dto.HouseDetailResponse;
import com.ssafy.home.house.dto.HouseSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "House", description = "주택 매물 API")
public interface HouseApiDocs {

    @Operation(
            summary = "주택 매물 검색",
            description = "지역 코드와 검색 조건으로 주택 매물 목록을 페이지 단위로 조회합니다."
    )
    PageResponse<HouseSummaryResponse> searchHouses(
            @Parameter(description = "법정동 지역 코드", example = "1111010100")
            @RequestParam String regionCode,
            @Parameter(description = "주택 유형", example = "APT")
            @RequestParam(required = false) String houseType,
            @Parameter(description = "거래 유형", example = "SALE")
            @RequestParam(required = false) String dealType,
            @Parameter(description = "최소 거래 금액", example = "10000")
            @RequestParam(required = false) Integer minAmount,
            @Parameter(description = "최대 거래 금액", example = "100000")
            @RequestParam(required = false) Integer maxAmount,
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size
    );

    @Operation(
            summary = "주택 매물 상세 조회",
            description = "주택 매물 ID로 상세 정보와 거래 이력을 조회합니다."
    )
    HouseDetailResponse getHouseDetail(
            @Parameter(description = "주택 매물 ID", example = "1")
            @PathVariable Long houseId
    );
}
