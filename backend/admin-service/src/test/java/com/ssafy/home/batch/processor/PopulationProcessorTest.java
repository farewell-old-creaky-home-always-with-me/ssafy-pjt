package com.ssafy.home.batch.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ssafy.home.external.seoul.demographics.SeoulRawPopulation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PopulationProcessorTest {

    private final PopulationProcessor processor = new PopulationProcessor();

    @Test
    @DisplayName("유효한 인구통계 데이터를 정규화한다")
    void processValidRow() throws Exception {
        SeoulRawPopulation raw = new SeoulRawPopulation(
                "서울특별시", "강남구", "역삼1동", "12345", "5678", "1234", "202505");

        var result = processor.process(raw);

        assertThat(result.sidoName()).isEqualTo("서울특별시");
        assertThat(result.sigunguName()).isEqualTo("강남구");
        assertThat(result.dongName()).isEqualTo("역삼1동");
        assertThat(result.totalPopulation()).isEqualTo(12345);
        assertThat(result.householdCount()).isEqualTo(5678);
        assertThat(result.seniorCount()).isEqualTo(1234);
        assertThat(result.referenceDate()).isEqualTo("202505");
    }

    @Test
    @DisplayName("숫자 필드가 비어 있으면 null로 처리한다")
    void processAllowsBlankCountFields() throws Exception {
        SeoulRawPopulation raw = new SeoulRawPopulation(
                "서울특별시", "강남구", "역삼1동", null, null, null, "202505");

        var result = processor.process(raw);

        assertThat(result.totalPopulation()).isNull();
        assertThat(result.householdCount()).isNull();
        assertThat(result.seniorCount()).isNull();
    }

    @Test
    @DisplayName("동 이름이 없으면 InvalidPopulationException을 던진다")
    void processThrowsWhenDongNameMissing() {
        SeoulRawPopulation raw = new SeoulRawPopulation(
                "서울특별시", "강남구", null, "12345", "5678", "1234", "202505");

        assertThatThrownBy(() -> processor.process(raw))
                .isInstanceOf(InvalidPopulationException.class);
    }

    @Test
    @DisplayName("기준연월이 없으면 InvalidPopulationException을 던진다")
    void processThrowsWhenReferenceDateMissing() {
        SeoulRawPopulation raw = new SeoulRawPopulation(
                "서울특별시", "강남구", "역삼1동", "12345", "5678", "1234", null);

        assertThatThrownBy(() -> processor.process(raw))
                .isInstanceOf(InvalidPopulationException.class);
    }
}
