package com.ssafy.home.housinginfo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ssafy.home.housinginfo.dto.HousingInfoResponse;
import com.ssafy.home.housinginfo.mapper.HousingInfoMapper;
import com.ssafy.home.housinginfo.mapper.dto.HousingInfoResult;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HousingInfoServiceTest {

    @Mock
    private HousingInfoMapper housingInfoMapper;

    private HousingInfoService housingInfoService;

    @BeforeEach
    void setUp() {
        housingInfoService = new HousingInfoService(housingInfoMapper);
    }

    @Test
    @DisplayName("공백 type은 null로 정규화하고 기본 limit 20으로 조회한다")
    void blankTypeAndNullLimitNormalizeToDefaults() {
        // given
        given(housingInfoMapper.findRecent(null, 20)).willReturn(List.of(housingInfoResult()));

        // when
        List<HousingInfoResponse> result = housingInfoService.getHousingInfo("   ", null);

        // then
        assertThat(result).hasSize(1);
        then(housingInfoMapper).should().findRecent(null, 20);
    }

    @Test
    @DisplayName("type은 trim 후 대문자로 정규화한다")
    void typeIsTrimmedAndUppercased() {
        // given
        given(housingInfoMapper.findRecent("POLICY", 10)).willReturn(List.of(housingInfoResult()));

        // when
        List<HousingInfoResponse> result = housingInfoService.getHousingInfo(" policy ", 10);

        // then
        assertThat(result).hasSize(1);
        then(housingInfoMapper).should().findRecent("POLICY", 10);
    }

    @Test
    @DisplayName("0 이하 limit은 기본값 20으로 조회한다")
    void nonPositiveLimitUsesDefaultLimit() {
        // given
        given(housingInfoMapper.findRecent(null, 20)).willReturn(List.of(housingInfoResult()));

        // when
        List<HousingInfoResponse> result = housingInfoService.getHousingInfo(null, -1);

        // then
        assertThat(result).hasSize(1);
        then(housingInfoMapper).should().findRecent(null, 20);
    }

    @Test
    @DisplayName("50을 초과한 limit은 50으로 제한한다")
    void limitGreaterThanMaxIsCapped() {
        // given
        given(housingInfoMapper.findRecent(null, 50)).willReturn(List.of(housingInfoResult()));

        // when
        List<HousingInfoResponse> result = housingInfoService.getHousingInfo(null, 100);

        // then
        assertThat(result).hasSize(1);
        then(housingInfoMapper).should().findRecent(null, 50);
    }

    @Test
    @DisplayName("주거 정보 조회 결과를 응답 DTO로 변환한다")
    void mapsResultToResponse() {
        // given
        given(housingInfoMapper.findRecent("POLICY", 1)).willReturn(List.of(housingInfoResult()));

        // when
        List<HousingInfoResponse> result = housingInfoService.getHousingInfo("POLICY", 1);

        // then
        assertThat(result)
                .singleElement()
                .satisfies(response -> {
                    assertThat(response.id()).isEqualTo(1L);
                    assertThat(response.title()).isEqualTo("청년 전세 지원");
                    assertThat(response.content()).isEqualTo("지원 내용");
                    assertThat(response.sourceName()).isEqualTo("주거복지포털");
                    assertThat(response.sourceUrl()).isEqualTo("https://example.com/info/1");
                    assertThat(response.infoType()).isEqualTo("POLICY");
                    assertThat(response.publishedAt()).isEqualTo(LocalDateTime.of(2026, 6, 25, 10, 0));
                });
    }

    private HousingInfoResult housingInfoResult() {
        HousingInfoResult result = new HousingInfoResult();
        result.setId(1L);
        result.setTitle("청년 전세 지원");
        result.setContent("지원 내용");
        result.setSourceName("주거복지포털");
        result.setSourceUrl("https://example.com/info/1");
        result.setInfoType("POLICY");
        result.setPublishedAt(LocalDateTime.of(2026, 6, 25, 10, 0));
        return result;
    }
}
