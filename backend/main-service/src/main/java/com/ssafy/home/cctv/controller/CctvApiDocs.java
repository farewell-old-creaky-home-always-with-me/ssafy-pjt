package com.ssafy.home.cctv.controller;

import com.ssafy.home.cctv.dto.CctvResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "CCTV", description = "CCTV 설치 현황 API")
public interface CctvApiDocs {

    @Operation(
            summary = "주변 CCTV 설치 현황 조회",
            description = "좌표와 반경 조건으로 주변 CCTV 설치 현황을 조회합니다."
    )
    ResponseEntity<List<CctvResponse>> getCctvInfos(
            @Parameter(description = "위도", example = "37.5665")
            @RequestParam BigDecimal lat,
            @Parameter(description = "경도", example = "126.9780")
            @RequestParam BigDecimal lng,
            @Parameter(description = "검색 반경 미터", example = "1000")
            @RequestParam(required = false) Integer radius
    );
}
