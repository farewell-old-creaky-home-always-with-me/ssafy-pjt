package com.ssafy.home.batch.skip;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ssafy.home.batch.processor.InvalidHouseDealException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;

class HouseDealSkipPolicyTest {

    @Test
    @DisplayName("지역코드 미매칭은 스킵 한도와 별도로 스킵한다")
    void unknownRegionCodeDoesNotConsumeInvalidDataSkipLimit() throws Exception {
        // Given
        HouseDealSkipPolicy policy = new HouseDealSkipPolicy(1);

        // When / Then
        assertThat(policy.shouldSkip(
                new InvalidHouseDealException("UNKNOWN_REGION_CODE", "Unknown region code"),
                1000
        )).isTrue();
        assertThat(policy.shouldSkip(
                new InvalidHouseDealException("UNKNOWN_REGION_CODE", "Unknown region code"),
                1001
        )).isTrue();
    }

    @Test
    @DisplayName("일반 데이터 오류는 지정된 스킵 한도를 넘으면 실패한다")
    void invalidDataErrorsFailAfterLimit() throws Exception {
        // Given
        HouseDealSkipPolicy policy = new HouseDealSkipPolicy(1);

        // When / Then
        assertThat(policy.shouldSkip(
                new InvalidHouseDealException("INVALID_AMOUNT", "Invalid deal amount"),
                0
        )).isTrue();
        assertThatThrownBy(() -> policy.shouldSkip(
                new InvalidHouseDealException("INVALID_AMOUNT", "Invalid deal amount"),
                1
        )).isInstanceOf(SkipLimitExceededException.class);
    }
}
