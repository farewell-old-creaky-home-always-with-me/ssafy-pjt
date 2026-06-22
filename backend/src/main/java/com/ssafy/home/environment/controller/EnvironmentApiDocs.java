package com.ssafy.home.environment.controller;

import com.ssafy.home.environment.dto.EnvironmentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Environment", description = "환경 정보 API")
public interface EnvironmentApiDocs {

    @Operation(
            summary = "주변 환경 정보 조회",
            description = "좌표와 반경 조건으로 주변 환경 정보를 조회합니다."
    )
    ResponseEntity<List<EnvironmentResponse>> getEnvironmentInfos(
            @Parameter(description = "위도", example = "37.5665")
            @RequestParam BigDecimal lat,
            @Parameter(description = "경도", example = "126.9780")
            @RequestParam BigDecimal lng,
            @Parameter(description = "검색 반경 미터", example = "1000")
            @RequestParam(required = false) Integer radius
    );
}
