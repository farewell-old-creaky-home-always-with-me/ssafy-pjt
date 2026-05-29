package com.ssafy.home.favorite.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.ssafy.home.favorite.dto.CreateFavoriteRequest;
import com.ssafy.home.favorite.mapper.FavoriteMapper;
import com.ssafy.home.global.exception.DuplicateResourceException;
import com.ssafy.home.global.exception.ForbiddenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteMapper favoriteMapper;

    private FavoriteService favoriteService;

    @BeforeEach
    void setUp() {
        favoriteService = new FavoriteService(favoriteMapper);
    }

    @Test
    void createFavoriteThrowsWhenDuplicateExists() {
        when(favoriteMapper.existsRegionCode("1100000000")).thenReturn(true);
        when(favoriteMapper.existsByMemberIdAndRegionCode(1L, "1100000000")).thenReturn(true);

        assertThatThrownBy(() -> favoriteService.createFavorite(1L, new CreateFavoriteRequest("1100000000")))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("이미 등록된 관심 지역입니다");
    }

    @Test
    void deleteFavoriteThrowsWhenOwnerDiffers() {
        when(favoriteMapper.findOwnerMemberIdByFavoriteId(3L)).thenReturn(2L);

        assertThatThrownBy(() -> favoriteService.deleteFavorite(1L, 3L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("본인 관심 지역만 삭제할 수 있습니다");
    }
}
