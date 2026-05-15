package com.ssafy.home.favorite.controller;

import com.ssafy.home.favorite.dto.CreateFavoriteRequest;
import com.ssafy.home.favorite.dto.FavoriteCreateResponse;
import com.ssafy.home.favorite.dto.FavoriteResponse;
import com.ssafy.home.favorite.service.FavoriteService;
import com.ssafy.home.global.interceptor.LoginRequired;
import com.ssafy.home.global.response.ApiResponse;
import com.ssafy.home.global.response.ItemsResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LoginRequired
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public ApiResponse<ItemsResponse<FavoriteResponse>> getFavorites(HttpSession session) {
        return ApiResponse.success(favoriteService.getFavorites(getMemberId(session)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FavoriteCreateResponse>> createFavorite(
            @Valid @RequestBody CreateFavoriteRequest request,
            HttpSession session
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(favoriteService.createFavorite(getMemberId(session), request)));
    }

    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long favoriteId, HttpSession session) {
        favoriteService.deleteFavorite(getMemberId(session), favoriteId);
        return ResponseEntity.noContent().build();
    }

    private Long getMemberId(HttpSession session) {
        return (Long) session.getAttribute("memberId");
    }
}
