package com.ssafy.home.place.controller;

import com.ssafy.home.place.dto.CreatePlaceRequest;
import com.ssafy.home.place.dto.PlaceResponse;
import com.ssafy.home.place.dto.UpdatePlaceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Place", description = "회원 장소 API")
public interface PlaceApiDocs {

    @Operation(summary = "내 장소 목록 조회", description = "현재 로그인한 회원의 집, 회사, 기타 장소 목록을 조회합니다.")
    List<PlaceResponse> getPlaces(@Parameter(hidden = true) HttpSession session);

    @Operation(summary = "내 장소 등록", description = "현재 로그인한 회원의 장소를 등록합니다.")
    ResponseEntity<PlaceResponse> createPlace(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "장소 등록 요청", required = true)
            @Valid @RequestBody CreatePlaceRequest request,
            @Parameter(hidden = true) HttpSession session
    );

    @Operation(summary = "내 장소 수정", description = "현재 로그인한 회원의 장소를 수정합니다.")
    PlaceResponse updatePlace(
            @Parameter(description = "장소 ID", example = "1")
            @PathVariable Long placeId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "장소 수정 요청", required = true)
            @Valid @RequestBody UpdatePlaceRequest request,
            @Parameter(hidden = true) HttpSession session
    );

    @Operation(summary = "내 장소 삭제", description = "현재 로그인한 회원의 장소를 삭제합니다.")
    ResponseEntity<Void> deletePlace(
            @Parameter(description = "장소 ID", example = "1")
            @PathVariable Long placeId,
            @Parameter(hidden = true) HttpSession session
    );
}
