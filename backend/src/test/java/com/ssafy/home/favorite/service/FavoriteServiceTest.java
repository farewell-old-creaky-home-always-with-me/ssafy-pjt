package com.ssafy.home.favorite.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import static com.ssafy.home.global.exception.ErrorCode.FAVORITE_DUPLICATE;
import static com.ssafy.home.global.exception.ErrorCode.FAVORITE_FORBIDDEN;

import com.ssafy.home.favorite.dto.FavoriteCreateRequest;
import com.ssafy.home.favorite.mapper.FavoriteMapper;
import com.ssafy.home.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("이미 등록된 관심 지역을 추가하면 예외가 발생한다")
    void createFavoriteThrowsWhenDuplicateExists() {
        // given
        given(favoriteMapper.existsByRegionCode("1100000000")).willReturn(true);
        given(favoriteMapper.existsByMemberIdAndRegionCode(1L, "1100000000")).willReturn(true);

        // when / then
        assertThatThrownBy(() -> favoriteService.createFavorite(1L, new FavoriteCreateRequest("1100000000")))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(FAVORITE_DUPLICATE))
                .hasMessage("이미 등록된 관심 지역입니다");
    }

    @Test
    @DisplayName("본인 소유가 아닌 관심 지역을 삭제하면 예외가 발생한다")
    void deleteFavoriteThrowsWhenOwnerDiffers() {
        // given
        given(favoriteMapper.findMemberIdById(3L)).willReturn(2L);

        // when / then
        assertThatThrownBy(() -> favoriteService.deleteFavorite(1L, 3L))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(FAVORITE_FORBIDDEN))
                .hasMessage("본인 관심 지역만 삭제할 수 있습니다");
    }
}
