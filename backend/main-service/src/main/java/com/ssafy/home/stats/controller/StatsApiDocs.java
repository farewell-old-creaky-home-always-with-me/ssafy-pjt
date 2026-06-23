package com.ssafy.home.stats.controller;

import com.ssafy.home.stats.dto.StatsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Stats", description = "부동산 통계 API")
public interface StatsApiDocs {

    @Operation(
            summary = "홈 화면 부동산 통계 조회",
            description = "이번 달 거래량, 이번 달 평균 매매가·전세가 및 전월 대비 변동률을 반환합니다."
    )
    ResponseEntity<StatsResponse> getStats();
}
