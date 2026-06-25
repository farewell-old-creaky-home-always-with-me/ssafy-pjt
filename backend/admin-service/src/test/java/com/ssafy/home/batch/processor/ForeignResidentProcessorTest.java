package com.ssafy.home.batch.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ssafy.home.external.seoul.demographics.SeoulRawForeignResident;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ForeignResidentProcessorTest {

    private final ForeignResidentProcessor processor = new ForeignResidentProcessor();

    @Test
    @DisplayName("유효한 외국인통계 데이터를 정규화한다")
    void processValidRow() throws Exception {
        SeoulRawForeignResident raw = new SeoulRawForeignResident(
                "서울특별시", "강남구", "역삼1동", "345", "202505");

        var result = processor.process(raw);

        assertThat(result.sidoName()).isEqualTo("서울특별시");
        assertThat(result.sigunguName()).isEqualTo("강남구");
        assertThat(result.dongName()).isEqualTo("역삼1동");
        assertThat(result.foreignCount()).isEqualTo(345);
        assertThat(result.referenceDate()).isEqualTo("202505");
    }

    @Test
    @DisplayName("외국인 수가 비어 있으면 null로 처리한다")
    void processAllowsBlankForeignCount() throws Exception {
        SeoulRawForeignResident raw = new SeoulRawForeignResident(
                "서울특별시", "강남구", "역삼1동", null, "202505");

        var result = processor.process(raw);

        assertThat(result.foreignCount()).isNull();
    }

    @Test
    @DisplayName("동 이름이 없으면 InvalidForeignResidentException을 던진다")
    void processThrowsWhenDongNameMissing() {
        SeoulRawForeignResident raw = new SeoulRawForeignResident(
                "서울특별시", "강남구", null, "345", "202505");

        assertThatThrownBy(() -> processor.process(raw))
                .isInstanceOf(InvalidForeignResidentException.class);
    }
}
