package com.ssafy.home.batch.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ssafy.home.batch.domain.NormalizedRegionCode;
import com.ssafy.home.external.vworld.VworldRawRegion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegionCodeProcessorTest {

    private RegionCodeProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new RegionCodeProcessor();
    }

    @Test
    @DisplayName("유효한 법정동을 정규화한다")
    void processValidRegion() {
        // given
        VworldRawRegion raw = new VworldRawRegion(
                "1111010100", "서울특별시", "종로구", "청운동", false
        );

        // when
        NormalizedRegionCode result = processor.process(raw);

        // then
        assertThat(result.regionCode()).isEqualTo("1111010100");
        assertThat(result.sidoName()).isEqualTo("서울특별시");
        assertThat(result.sigunguName()).isEqualTo("종로구");
        assertThat(result.dongName()).isEqualTo("청운동");
    }

    @Test
    @DisplayName("폐지된 법정동은 스킵한다")
    void skipAbolishedRegion() {
        // given
        VworldRawRegion raw = new VworldRawRegion(
                "1111010100", "서울특별시", "종로구", "청운동", true
        );

        // when / then
        assertThatThrownBy(() -> processor.process(raw))
                .isInstanceOf(InvalidRegionCodeException.class);
    }

    @Test
    @DisplayName("법정동코드 형식이 잘못되면 스킵한다")
    void skipInvalidRegionCode() {
        // given
        VworldRawRegion raw = new VworldRawRegion(
                "11110", "서울특별시", "종로구", "청운동", false
        );

        // when / then
        assertThatThrownBy(() -> processor.process(raw))
                .isInstanceOf(InvalidRegionCodeException.class);
    }
}
