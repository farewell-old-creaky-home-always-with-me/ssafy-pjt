package com.ssafy.home.batch.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ssafy.home.external.seoul.SeoulRawEnvironment;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EnvironmentProcessorTest {

    private final EnvironmentProcessor processor = new EnvironmentProcessor();

    @Test
    @DisplayName("서울 열린데이터 환경 정보를 정규화한다")
    void processValidEnvironmentRow() throws Exception {
        SeoulRawEnvironment raw = new SeoulRawEnvironment(
                "air",
                "대기 배출",
                "테스트 배출시설",
                "12.3400",
                "kg",
                "2026-05-30",
                "37.5665000",
                "126.9780000"
        );

        var result = processor.process(raw);

        assertThat(result.itemName()).isEqualTo("대기 배출 - 테스트 배출시설");
        assertThat(result.value()).isEqualByComparingTo(new BigDecimal("12.3400"));
        assertThat(result.unit()).isEqualTo("kg");
        assertThat(result.measuredDate()).isEqualTo(LocalDate.of(2026, 5, 30));
        assertThat(result.latitude()).isEqualByComparingTo(new BigDecimal("37.5665000"));
        assertThat(result.longitude()).isEqualByComparingTo(new BigDecimal("126.9780000"));
    }

    @Test
    @DisplayName("측정 수치가 비어 있으면 null로 정규화한다")
    void processAllowsBlankValue() throws Exception {
        SeoulRawEnvironment raw = new SeoulRawEnvironment(
                "green",
                "녹지",
                "테스트 녹지",
                "",
                null,
                "",
                "37.5665000",
                "126.9780000"
        );

        var result = processor.process(raw);

        assertThat(result.value()).isNull();
        assertThat(result.unit()).isNull();
        assertThat(result.measuredDate()).isNull();
    }

    @Test
    @DisplayName("위도가 숫자가 아니면 InvalidEnvironmentException을 던진다")
    void processThrowsWhenLatitudeInvalid() {
        SeoulRawEnvironment raw = new SeoulRawEnvironment(
                "green",
                "녹지",
                "테스트 녹지",
                null,
                null,
                null,
                "invalid",
                "126.9780000"
        );

        assertThatThrownBy(() -> processor.process(raw))
                .isInstanceOf(InvalidEnvironmentException.class);
    }
}
