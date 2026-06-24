package com.ssafy.home.batch.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ssafy.home.external.sdsc.SdscRawStore;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommercialAreaProcessorTest {

    private final CommercialAreaProcessor processor = new CommercialAreaProcessor();

    @Test
    @DisplayName("유효한 상가 정보를 정규화한다")
    void processValidStore() throws Exception {
        SdscRawStore raw = new SdscRawStore(
                "J1100000001", "테스트식당",
                "음식", "한식", "한식음식점",
                "37.575023", "126.977957",
                "서울 종로구 1-1", null
        );

        var result = processor.process(raw);

        assertThat(result.bizId()).isEqualTo("J1100000001");
        assertThat(result.bizName()).isEqualTo("테스트식당");
        assertThat(result.latitude()).isEqualByComparingTo(new BigDecimal("37.575023"));
        assertThat(result.longitude()).isEqualByComparingTo(new BigDecimal("126.977957"));
        assertThat(result.address()).isEqualTo("서울 종로구 1-1");
    }

    @Test
    @DisplayName("bizesId가 없으면 InvalidCommercialAreaException을 던진다")
    void processThrowsWhenBizesIdMissing() {
        SdscRawStore raw = new SdscRawStore(
                null, "테스트식당",
                null, null, null,
                "37.575023", "126.977957",
                null, null
        );

        assertThatThrownBy(() -> processor.process(raw))
                .isInstanceOf(InvalidCommercialAreaException.class);
    }

    @Test
    @DisplayName("위도가 숫자가 아니면 InvalidCommercialAreaException을 던진다")
    void processThrowsWhenLatInvalid() {
        SdscRawStore raw = new SdscRawStore(
                "J1100000001", "테스트식당",
                null, null, null,
                "invalid", "126.977957",
                null, null
        );

        assertThatThrownBy(() -> processor.process(raw))
                .isInstanceOf(InvalidCommercialAreaException.class);
    }

    @Test
    @DisplayName("도로명 주소가 없으면 지번 주소를 사용한다")
    void processFallsBackToLnoAdr() throws Exception {
        SdscRawStore raw = new SdscRawStore(
                "J1100000001", "테스트식당",
                null, null, null,
                "37.575023", "126.977957",
                null, "서울 종로구 지번주소"
        );

        var result = processor.process(raw);

        assertThat(result.address()).isEqualTo("서울 종로구 지번주소");
    }
}
