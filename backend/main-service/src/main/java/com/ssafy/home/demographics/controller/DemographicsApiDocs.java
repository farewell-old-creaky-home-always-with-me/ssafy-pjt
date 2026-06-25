package com.ssafy.home.demographics.controller;

import com.ssafy.home.demographics.dto.DemographicsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Demographics", description = "동네 구성원 통계 API")
public interface DemographicsApiDocs {

    @Operation(
            summary = "동네 구성원 통계 조회",
            description = "시도·시군구·동 이름으로 해당 동의 최신 인구 통계를 조회합니다."
    )
    ResponseEntity<DemographicsResponse> getDemographics(
            @Parameter(description = "시도명", example = "서울특별시") @RequestParam String sido,
            @Parameter(description = "시군구명", example = "강남구") @RequestParam String sigungu,
            @Parameter(description = "행정동명", example = "역삼1동") @RequestParam String dong
    );
}
