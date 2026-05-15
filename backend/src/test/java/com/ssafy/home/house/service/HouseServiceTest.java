package com.ssafy.home.house.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.ssafy.home.global.exception.ResourceNotFoundException;
import com.ssafy.home.global.exception.ValidationException;
import com.ssafy.home.house.mapper.HouseMapper;
import org.junit.jupiter.api.BeforeEach;
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
    void searchHousesThrowsWhenRegionCodeIsInvalid() {
        when(houseMapper.existsRegionCode("1100000000")).thenReturn(false);

        assertThatThrownBy(() -> houseService.searchHouses("1100000000", null, null, null, null, 1, 20))
                .isInstanceOf(ValidationException.class)
                .hasMessage("유효하지 않은 행정구역 코드입니다");
    }

    @Test
    void getHouseDetailThrowsWhenHouseDoesNotExist() {
        when(houseMapper.findHouseById(99L)).thenReturn(null);

        assertThatThrownBy(() -> houseService.getHouseDetail(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("해당 주택을 찾을 수 없습니다");
    }
}
