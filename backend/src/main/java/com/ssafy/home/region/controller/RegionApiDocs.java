package com.ssafy.home.region.controller;

import com.ssafy.home.region.dto.RegionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Region", description = "행정구역 API")
public interface RegionApiDocs {

    @Operation(
            summary = "행정구역 목록 조회",
            description = "매물이 있는 행정구역 목록을 조회합니다. dong 파라미터로 동 이름을 검색할 수 있습니다."
    )
    ResponseEntity<List<RegionResponse>> getRegions(
            @Parameter(description = "동 이름 검색어 (부분 일치)", example = "역삼")
            @RequestParam(required = false) String dong
    );
}
