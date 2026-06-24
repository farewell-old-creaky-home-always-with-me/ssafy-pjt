package com.ssafy.home.house.controller;

import com.ssafy.home.global.response.PageResponse;
import com.ssafy.home.house.dto.HouseDetailResponse;
import com.ssafy.home.house.dto.HouseSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "House", description = "주택 매물 API")
public interface HouseApiDocs {

    @Operation(
            summary = "주택 매물 검색",
            description = "지역 코드와 검색 조건으로 주택 매물 목록을 페이지 단위로 조회합니다."
    )
    ResponseEntity<PageResponse<HouseSummaryResponse>> searchHouses(
            @Parameter(description = "법정동 지역 코드", example = "1111010100")
            @RequestParam String regionCode,
            @Parameter(description = "아파트명", example = "래미안")
            @RequestParam(required = false) String houseName,
            @Parameter(description = "주택 유형 (아파트, 다세대)", example = "아파트")
            @RequestParam(required = false) String houseType,
            @Parameter(description = "거래 유형 (매매, 전세, 월세, 전월세)", example = "매매")
            @RequestParam(required = false) String dealType,
            @Parameter(description = "최소 거래 금액 (만원)", example = "10000")
            @RequestParam(required = false) Integer minAmount,
            @Parameter(description = "최대 거래 금액 (만원)", example = "100000")
            @RequestParam(required = false) Integer maxAmount,
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬 기준 (name, area, floor, price, date)", example = "date")
            @RequestParam(defaultValue = "date") String sortBy,
            @Parameter(description = "정렬 방향 (asc, desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir
    );

    @Operation(
            summary = "주택 매물 상세 조회",
            description = "주택 매물 ID로 상세 정보와 거래 이력을 조회합니다."
    )
    ResponseEntity<HouseDetailResponse> getHouseDetail(
            @Parameter(description = "주택 매물 ID", example = "1")
            @PathVariable Long houseId
    );
}
