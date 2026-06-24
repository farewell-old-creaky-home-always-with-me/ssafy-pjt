package com.ssafy.home.member.service;

import static com.ssafy.home.global.exception.ErrorCode.COMMON_INVALID_INPUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ssafy.home.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PhoneNumberNormalizerTest {

    @Test
    @DisplayName("전화번호의 공백과 하이픈을 제거한다")
    void normalizeRemovesSpacesAndHyphens() {
        assertThat(PhoneNumberNormalizer.normalize("010-1234 5678")).isEqualTo("01012345678");
    }

    @Test
    @DisplayName("정규화 후 전화번호가 비어 있으면 예외가 발생한다")
    void normalizeThrowsWhenNormalizedPhoneIsBlank() {
        assertThatThrownBy(() -> PhoneNumberNormalizer.normalize("---   "))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(COMMON_INVALID_INPUT));
    }

    @Test
    @DisplayName("정규화 후 전화번호가 20자를 초과하면 예외가 발생한다")
    void normalizeThrowsWhenNormalizedPhoneIsTooLong() {
        assertThatThrownBy(() -> PhoneNumberNormalizer.normalize("012345678901234567890"))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(COMMON_INVALID_INPUT));
    }
}
