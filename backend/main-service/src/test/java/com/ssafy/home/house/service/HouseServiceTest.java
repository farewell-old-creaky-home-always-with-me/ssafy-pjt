package com.ssafy.home.house.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import static com.ssafy.home.global.exception.ErrorCode.COMMON_INVALID_INPUT;
import static com.ssafy.home.global.exception.ErrorCode.HOUSE_INVALID_REGION;
import static com.ssafy.home.global.exception.ErrorCode.HOUSE_NOT_FOUND;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.house.mapper.HouseMapper;
import com.ssafy.home.house.mapper.dto.HouseSearchParam;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    @DisplayName("아파트명 검색 조건을 전달한다")
    void searchHousesPassesHouseNameCondition() {
        // given
        given(houseMapper.existsByRegionCode("1168010100")).willReturn(true);
        given(houseMapper.countBySearch(any(HouseSearchParam.class))).willReturn(0L);
        given(houseMapper.search(any(HouseSearchParam.class))).willReturn(List.of());

        // when
        houseService.searchHouses("1168010100", " 래미안 ", null, null, null, null, 1, 20, "date", "desc");

        // then
        ArgumentCaptor<HouseSearchParam> captor = ArgumentCaptor.forClass(HouseSearchParam.class);
        then(houseMapper).should().countBySearch(captor.capture());
        assertThat(captor.getValue().getHouseName()).isEqualTo("래미안");
    }

    @Test
    @DisplayName("유효하지 않은 행정구역 코드로 검색하면 예외가 발생한다")
    void searchHousesThrowsWhenRegionCodeIsInvalid() {
        // given
        given(houseMapper.existsByRegionCode("1100000000")).willReturn(false);

        // when / then
        assertThatThrownBy(() -> houseService.searchHouses("1100000000", null, null, null, null, null, 1, 20, "date", "desc"))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(HOUSE_INVALID_REGION))
                .hasMessage("유효하지 않은 행정구역 코드입니다");
    }

    @Test
    @DisplayName("유효하지 않은 sortBy 값으로 검색하면 예외가 발생한다")
    void searchHousesThrowsWhenSortByIsInvalid() {
        // given
        given(houseMapper.existsByRegionCode("1100000000")).willReturn(true);

        // when / then
        assertThatThrownBy(() -> houseService.searchHouses("1100000000", null, null, null, null, null, 1, 20, "INVALID", "desc"))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(COMMON_INVALID_INPUT));
    }

    @Test
    @DisplayName("유효하지 않은 sortDir 값으로 검색하면 예외가 발생한다")
    void searchHousesThrowsWhenSortDirIsInvalid() {
        // given
        given(houseMapper.existsByRegionCode("1100000000")).willReturn(true);

        // when / then
        assertThatThrownBy(() -> houseService.searchHouses("1100000000", null, null, null, null, null, 1, 20, "date", "INVALID"))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(COMMON_INVALID_INPUT));
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
