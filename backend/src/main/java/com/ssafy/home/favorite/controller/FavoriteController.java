package com.ssafy.home.favorite.controller;

import com.ssafy.home.favorite.dto.CreateFavoriteRequest;
import com.ssafy.home.favorite.dto.FavoriteCreateResponse;
import com.ssafy.home.favorite.dto.FavoriteResponse;
import com.ssafy.home.favorite.service.FavoriteService;
import com.ssafy.home.global.auth.LoginMemberId;
import com.ssafy.home.global.interceptor.LoginRequired;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class FavoriteController implements FavoriteApiDocs {

    private final FavoriteService favoriteService;

    @GetMapping
    @Override
    public List<FavoriteResponse> getFavorites(@LoginMemberId Long memberId) {
        return favoriteService.getFavorites(memberId);
    }

    @PostMapping
    @Override
    public ResponseEntity<FavoriteCreateResponse> createFavorite(
            @Valid @RequestBody CreateFavoriteRequest request,
            @LoginMemberId Long memberId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(favoriteService.createFavorite(memberId, request));
    }

    @DeleteMapping("/{favoriteId}")
    @Override
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long favoriteId, @LoginMemberId Long memberId) {
        favoriteService.deleteFavorite(memberId, favoriteId);
        return ResponseEntity.noContent().build();
    }
}
