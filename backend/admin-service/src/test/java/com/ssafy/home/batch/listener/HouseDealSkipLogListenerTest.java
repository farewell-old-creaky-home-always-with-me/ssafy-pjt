package com.ssafy.home.batch.listener;

import static org.assertj.core.api.Assertions.assertThatCode;

import com.ssafy.home.batch.processor.InvalidHouseDealException;
import com.ssafy.home.external.molit.MolitRawHouseDeal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HouseDealSkipLogListenerTest {

    @Test
    @DisplayName("StepExecution이 없어도 스킵 로그 listener는 예외를 던지지 않는다")
    void onSkipInProcessDoesNotFailWithoutStepExecution() {
        // Given
        HouseDealSkipLogListener listener = new HouseDealSkipLogListener();
        MolitRawHouseDeal rawDeal = new MolitRawHouseDeal(
                null,
                "26260",
                "거제동",
                "아시아드코오롱하늘채",
                "1528",
                "100,000",
                "2025",
                "1",
                "1",
                "84.0",
                "10",
                "2020"
        );

        // When / Then
        assertThatCode(() -> listener.onSkipInProcess(
                rawDeal,
                new InvalidHouseDealException("UNKNOWN_REGION_CODE", "Unknown region code")
        )).doesNotThrowAnyException();
    }
}
