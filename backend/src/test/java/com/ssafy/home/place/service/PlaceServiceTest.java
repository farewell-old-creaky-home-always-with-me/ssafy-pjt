package com.ssafy.home.place.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.exception.ErrorCode;
import com.ssafy.home.place.dto.CreatePlaceRequest;
import com.ssafy.home.place.mapper.dto.PlaceParam;
import com.ssafy.home.place.mapper.dto.PlaceResult;
import com.ssafy.home.place.dto.PlaceType;
import com.ssafy.home.place.dto.UpdatePlaceRequest;
import com.ssafy.home.place.mapper.PlaceMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock
    private PlaceMapper placeMapper;

    private PlaceService placeService;

    @BeforeEach
    void setUp() {
        placeService = new PlaceService(placeMapper);
    }

    @Test
    void createPlaceThrowsWhenHomeAlreadyExists() {
        when(placeMapper.countByMemberIdAndType(1L, PlaceType.HOME.name())).thenReturn(1);

        assertThatThrownBy(() -> placeService.createPlace(1L, new CreatePlaceRequest(
                "HOME",
                "우리집",
                "서울특별시 강남구 테헤란로 212",
                null,
                new BigDecimal("37.5012743"),
                new BigDecimal("127.0395850")
        )))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(ErrorCode.PLACE_DUPLICATE_TYPE))
                .hasMessage("이미 등록된 장소 유형입니다");
    }

    @Test
    void createPlaceThrowsWhenOtherLimitExceeded() {
        when(placeMapper.countByMemberIdAndType(1L, PlaceType.OTHER.name())).thenReturn(5);

        assertThatThrownBy(() -> placeService.createPlace(1L, new CreatePlaceRequest(
                "OTHER",
                "헬스장",
                "서울특별시 강남구 테헤란로 300",
                null,
                new BigDecimal("37.5030000"),
                new BigDecimal("127.0400000")
        )))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(ErrorCode.PLACE_OTHER_LIMIT_EXCEEDED))
                .hasMessage("기타 장소는 최대 5개까지 등록할 수 있습니다");
    }

    @Test
    void createPlaceThrowsWhenCoordinateIsInvalid() {
        assertThatThrownBy(() -> placeService.createPlace(1L, new CreatePlaceRequest(
                "WORK",
                "회사",
                "서울특별시 중구 세종대로 110",
                null,
                new BigDecimal("91.0000000"),
                new BigDecimal("127.0000000")
        )))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(ErrorCode.PLACE_INVALID_COORDINATE))
                .hasMessage("유효하지 않은 좌표입니다");
    }

    @Test
    void createPlaceReturnsSavedPlace() {
        when(placeMapper.countByMemberIdAndType(1L, PlaceType.HOME.name())).thenReturn(0);
        doAnswer(invocation -> {
            PlaceParam entity = invocation.getArgument(0);
            entity.setId(10L);
            return null;
        }).when(placeMapper).insert(any(PlaceParam.class));

        var response = placeService.createPlace(1L, new CreatePlaceRequest(
                "HOME",
                "  우리집  ",
                "  서울특별시 강남구 테헤란로 212  ",
                null,
                new BigDecimal("37.5012743"),
                new BigDecimal("127.0395850")
        ));

        assertThat(response.placeId()).isEqualTo(10L);
        assertThat(response.placeType()).isEqualTo("HOME");
        assertThat(response.name()).isEqualTo("우리집");
        assertThat(response.address()).isEqualTo("서울특별시 강남구 테헤란로 212");
    }

    @Test
    void updateThrowsWhenOwnerDiffers() {
        PlaceResult existing = new PlaceResult();
        existing.setId(3L);
        existing.setMemberId(2L);
        existing.setPlaceType(PlaceType.OTHER.name());
        when(placeMapper.findById(3L)).thenReturn(existing);

        assertThatThrownBy(() -> placeService.updatePlace(1L, 3L, new UpdatePlaceRequest(
                "OTHER",
                "카페",
                "서울특별시 마포구 양화로 10",
                null,
                new BigDecimal("37.5500000"),
                new BigDecimal("126.9200000")
        )))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(ErrorCode.PLACE_FORBIDDEN))
                .hasMessage("본인 장소만 수정하거나 삭제할 수 있습니다");
    }

    @Test
    void deletePlaceDeletesOnlyOwnerPlace() {
        PlaceResult existing = new PlaceResult();
        existing.setId(3L);
        existing.setMemberId(1L);
        existing.setPlaceType(PlaceType.OTHER.name());
        when(placeMapper.findById(3L)).thenReturn(existing);

        placeService.deletePlace(1L, 3L);

        verify(placeMapper).deleteByIdAndMemberId(3L, 1L);
    }
}
