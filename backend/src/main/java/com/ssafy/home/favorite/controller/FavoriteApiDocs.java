package com.ssafy.home.favorite.controller;

import com.ssafy.home.favorite.dto.CreateFavoriteRequest;
import com.ssafy.home.favorite.dto.FavoriteCreateResponse;
import com.ssafy.home.favorite.dto.FavoriteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Favorite", description = "관심 매물 API")
public interface FavoriteApiDocs {

    @Operation(
            summary = "관심 매물 목록 조회",
            description = "현재 로그인한 회원의 관심 매물 목록을 조회합니다."
    )
    List<FavoriteResponse> getFavorites(@Parameter(hidden = true) HttpSession session);

    @Operation(
            summary = "관심 매물 등록",
            description = "현재 로그인한 회원의 관심 매물을 등록합니다."
    )
    ResponseEntity<FavoriteCreateResponse> createFavorite(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "관심 매물 등록 요청", required = true)
            @Valid @RequestBody CreateFavoriteRequest request,
            @Parameter(hidden = true) HttpSession session
    );

    @Operation(
            summary = "관심 매물 삭제",
            description = "관심 매물 ID로 현재 로그인한 회원의 관심 매물을 삭제합니다."
    )
    ResponseEntity<Void> deleteFavorite(
            @Parameter(description = "관심 매물 ID", example = "1")
            @PathVariable Long favoriteId,
            @Parameter(hidden = true) HttpSession session
    );
}
