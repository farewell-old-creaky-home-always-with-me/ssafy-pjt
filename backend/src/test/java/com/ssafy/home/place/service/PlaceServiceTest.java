package com.ssafy.home.place.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.verify;

import static com.ssafy.home.global.exception.ErrorCode.PLACE_DUPLICATE_TYPE;
import static com.ssafy.home.global.exception.ErrorCode.PLACE_FORBIDDEN;
import static com.ssafy.home.global.exception.ErrorCode.PLACE_INVALID_COORDINATE;
import static com.ssafy.home.global.exception.ErrorCode.PLACE_OTHER_LIMIT_EXCEEDED;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.place.dto.PlaceCreateRequest;
import com.ssafy.home.place.dto.PlaceType;
import com.ssafy.home.place.dto.PlaceUpdateRequest;
import com.ssafy.home.place.mapper.PlaceMapper;
import com.ssafy.home.place.mapper.dto.PlaceCreateParam;
import com.ssafy.home.place.mapper.dto.PlaceResult;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    private static final String REGION_CODE = "1100000000";

    @Mock
    private PlaceMapper placeMapper;

    private PlaceService placeService;

    @BeforeEach
    void setUp() {
        placeService = new PlaceService(placeMapper);
    }

    @Test
    @DisplayName("이미 등록된 HOME 장소를 추가하면 예외가 발생한다")
    void createPlaceThrowsWhenHomeAlreadyExists() {
        // given
        given(placeMapper.existsByRegionCode(REGION_CODE)).willReturn(true);
        given(placeMapper.countByMemberIdAndType(1L, PlaceType.HOME.name())).willReturn(1);

        // when / then
        assertThatThrownBy(() -> placeService.createPlace(1L, homeCreateRequest()))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(PLACE_DUPLICATE_TYPE))
                .hasMessage("이미 등록된 장소 유형입니다");
    }

    @Test
    @DisplayName("OTHER 장소가 5개를 초과하면 예외가 발생한다")
    void createPlaceThrowsWhenOtherLimitExceeded() {
        // given
        given(placeMapper.existsByRegionCode(REGION_CODE)).willReturn(true);
        given(placeMapper.countByMemberIdAndType(1L, PlaceType.OTHER.name())).willReturn(5);

        // when / then
        assertThatThrownBy(() -> placeService.createPlace(1L, otherCreateRequest()))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(PLACE_OTHER_LIMIT_EXCEEDED))
                .hasMessage("기타 장소는 최대 5개까지 등록할 수 있습니다");
    }

    @Test
    @DisplayName("유효하지 않은 좌표로 장소를 등록하면 예외가 발생한다")
    void createPlaceThrowsWhenCoordinateIsInvalid() {
        // given
        given(placeMapper.existsByRegionCode(REGION_CODE)).willReturn(true);

        // when / then
        assertThatThrownBy(() -> placeService.createPlace(1L, invalidCoordinateRequest()))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(PLACE_INVALID_COORDINATE))
                .hasMessage("유효하지 않은 좌표입니다");
    }

    @Test
    @DisplayName("장소 등록에 성공하면 저장된 장소 정보를 반환한다")
    void createPlaceReturnsSavedPlace() {
        // given
        given(placeMapper.existsByRegionCode(REGION_CODE)).willReturn(true);
        given(placeMapper.countByMemberIdAndType(1L, PlaceType.HOME.name())).willReturn(0);
        willAnswer(invocation -> {
            PlaceCreateParam entity = invocation.getArgument(0);
            entity.setId(10L);
            return null;
        }).given(placeMapper).insert(any(PlaceCreateParam.class));

        // when
        var response = placeService.createPlace(1L, homeCreateRequestWithWhitespace());

        // then
        assertThat(response.placeId()).isEqualTo(10L);
        assertThat(response.placeType()).isEqualTo("HOME");
        assertThat(response.name()).isEqualTo("우리집");
        assertThat(response.address()).isEqualTo("서울특별시 강남구 테헤란로 212");
    }

    @Test
    @DisplayName("본인 소유가 아닌 장소를 수정하면 예외가 발생한다")
    void updateThrowsWhenOwnerDiffers() {
        // given
        given(placeMapper.findById(3L)).willReturn(placeResult(3L, 2L, PlaceType.OTHER.name()));

        // when / then
        assertThatThrownBy(() -> placeService.updatePlace(1L, 3L, otherUpdateRequest()))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(PLACE_FORBIDDEN))
                .hasMessage("본인 장소만 수정하거나 삭제할 수 있습니다");
    }

    @Test
    @DisplayName("본인 장소만 삭제한다")
    void deletePlaceDeletesOnlyOwnerPlace() {
        // given
        given(placeMapper.findById(3L)).willReturn(placeResult(3L, 1L, PlaceType.OTHER.name()));

        // when
        placeService.deletePlace(1L, 3L);

        // then
        verify(placeMapper).deleteByIdAndMemberId(3L, 1L);
    }

    private PlaceCreateRequest homeCreateRequest() {
        return new PlaceCreateRequest(
                "HOME",
                "우리집",
                "서울특별시 강남구 테헤란로 212",
                REGION_CODE,
                new BigDecimal("37.5012743"),
                new BigDecimal("127.0395850")
        );
    }

    private PlaceCreateRequest homeCreateRequestWithWhitespace() {
        return new PlaceCreateRequest(
                "HOME",
                "  우리집  ",
                "  서울특별시 강남구 테헤란로 212  ",
                REGION_CODE,
                new BigDecimal("37.5012743"),
                new BigDecimal("127.0395850")
        );
    }

    private PlaceCreateRequest otherCreateRequest() {
        return new PlaceCreateRequest(
                "OTHER",
                "헬스장",
                "서울특별시 강남구 테헤란로 300",
                REGION_CODE,
                new BigDecimal("37.5030000"),
                new BigDecimal("127.0400000")
        );
    }

    private PlaceCreateRequest invalidCoordinateRequest() {
        return new PlaceCreateRequest(
                "WORK",
                "회사",
                "서울특별시 중구 세종대로 110",
                REGION_CODE,
                new BigDecimal("91.0000000"),
                new BigDecimal("127.0000000")
        );
    }

    private PlaceUpdateRequest otherUpdateRequest() {
        return new PlaceUpdateRequest(
                "OTHER",
                "카페",
                "서울특별시 마포구 양화로 10",
                REGION_CODE,
                new BigDecimal("37.5500000"),
                new BigDecimal("126.9200000")
        );
    }

    private PlaceResult placeResult(Long placeId, Long memberId, String placeType) {
        PlaceResult existing = new PlaceResult();
        existing.setId(placeId);
        existing.setMemberId(memberId);
        existing.setPlaceType(placeType);
        return existing;
    }
}
