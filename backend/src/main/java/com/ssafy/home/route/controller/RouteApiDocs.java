package com.ssafy.home.route.controller;

import com.ssafy.home.global.auth.LoginMemberId;
import com.ssafy.home.route.dto.RouteRequest;
import com.ssafy.home.route.dto.RouteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Route", description = "경로 탐색 API")
public interface RouteApiDocs {

    @Operation(
            summary = "A* 최단 경로 탐색",
            description = "매물(houseId)과 내 장소(placeId) 사이의 최단 경로를 시설물 기반 A* 알고리즘으로 계산합니다. 결과는 DB에 저장됩니다."
    )
    RouteResponse calculateRoute(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "경로 탐색 요청", required = true)
            @Valid @RequestBody RouteRequest request,
            @Parameter(hidden = true) @LoginMemberId Long memberId
    );
}
