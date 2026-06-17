package com.ssafy.home.commercial.controller;

import com.ssafy.home.commercial.dto.CommercialResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Commercial", description = "상권 정보 API")
public interface CommercialApiDocs {

    @Operation(
            summary = "주변 상권 정보 조회",
            description = "좌표와 반경 조건으로 주변 상권 정보를 조회합니다."
    )
    ResponseEntity<List<CommercialResponse>> getCommercials(
            @Parameter(description = "위도", example = "37.5665")
            @RequestParam BigDecimal lat,
            @Parameter(description = "경도", example = "126.9780")
            @RequestParam BigDecimal lng,
            @Parameter(description = "검색 반경 미터", example = "1000")
            @RequestParam(required = false) Integer radius,
            @Parameter(description = "상권 카테고리", example = "음식")
            @RequestParam(required = false) String category
    );
}
