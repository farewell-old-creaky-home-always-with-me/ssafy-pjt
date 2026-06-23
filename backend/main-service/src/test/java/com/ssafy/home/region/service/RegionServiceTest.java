package com.ssafy.home.region.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ssafy.home.region.mapper.RegionMapper;
import com.ssafy.home.region.mapper.dto.RegionResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegionServiceTest {

    @Mock
    private RegionMapper regionMapper;

    private RegionService regionService;

    @BeforeEach
    void setUp() {
        regionService = new RegionService(regionMapper);
    }

    @Test
    @DisplayName("공백만 있는 dong은 null로 정규화되어 전체 조회한다")
    void blankDongNormalizesToNull() {
        // given
        given(regionMapper.findAll(null)).willReturn(List.of(regionResult()));

        // when
        var result = regionService.getRegions("   ");

        // then
        assertThat(result).hasSize(1);
        then(regionMapper).should().findAll(null);
    }

    @Test
    @DisplayName("앞뒤 공백이 포함된 dong은 trim되어 검색한다")
    void dongWithSurroundingWhitespaceIsTrimmed() {
        // given
        given(regionMapper.findAll("역삼")).willReturn(List.of(regionResult()));

        // when
        var result = regionService.getRegions("  역삼  ");

        // then
        assertThat(result).hasSize(1);
        then(regionMapper).should().findAll("역삼");
    }

    @Test
    @DisplayName("null dong은 그대로 전달되어 전체 조회한다")
    void nullDongPassesThrough() {
        // given
        given(regionMapper.findAll(null)).willReturn(List.of(regionResult()));

        // when
        var result = regionService.getRegions(null);

        // then
        assertThat(result).hasSize(1);
        then(regionMapper).should().findAll(null);
    }

    private RegionResult regionResult() {
        RegionResult r = new RegionResult();
        r.setRegionCode("1168010100");
        r.setSidoName("서울특별시");
        r.setSigunguName("강남구");
        r.setDongName("역삼동");
        return r;
    }
}
