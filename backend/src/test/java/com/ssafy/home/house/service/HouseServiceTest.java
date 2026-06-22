package com.ssafy.home.house.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import static com.ssafy.home.global.exception.ErrorCode.HOUSE_INVALID_REGION;
import static com.ssafy.home.global.exception.ErrorCode.HOUSE_NOT_FOUND;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.house.mapper.HouseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HouseServiceTest {

    @Mock
    private HouseMapper houseMapper;

    private HouseService houseService;

    @BeforeEach
    void setUp() {
        houseService = new HouseService(houseMapper);
    }

    @Test
    @DisplayName("유효하지 않은 행정구역 코드로 검색하면 예외가 발생한다")
    void searchHousesThrowsWhenRegionCodeIsInvalid() {
        // given
        given(houseMapper.existsByRegionCode("1100000000")).willReturn(false);

        // when / then
        assertThatThrownBy(() -> houseService.searchHouses("1100000000", null, null, null, null, 1, 20, "date", "desc"))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(HOUSE_INVALID_REGION))
                .hasMessage("유효하지 않은 행정구역 코드입니다");
    }

    @Test
    @DisplayName("존재하지 않는 주택을 조회하면 예외가 발생한다")
    void getHouseDetailThrowsWhenHouseDoesNotExist() {
        // given
        given(houseMapper.findById(99L)).willReturn(null);

        // when / then
        assertThatThrownBy(() -> houseService.getHouseDetail(99L))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(HOUSE_NOT_FOUND))
                .hasMessage("해당 주택을 찾을 수 없습니다");
    }
}
