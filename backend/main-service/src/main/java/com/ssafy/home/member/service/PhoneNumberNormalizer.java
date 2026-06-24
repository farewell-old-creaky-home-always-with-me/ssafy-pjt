package com.ssafy.home.member.service;

import static com.ssafy.home.global.exception.ErrorCode.COMMON_INVALID_INPUT;

import com.ssafy.home.global.exception.CustomException;

final class PhoneNumberNormalizer {

    private static final int MAX_PHONE_LENGTH = 20;

    private PhoneNumberNormalizer() {
    }

    static String normalize(String phone) {
        if (phone == null) {
            return null;
        }
        String normalizedPhone = phone.replaceAll("[\\s-]", "");
        if (normalizedPhone.isBlank() || normalizedPhone.length() > MAX_PHONE_LENGTH) {
            throw new CustomException(COMMON_INVALID_INPUT);
        }
        return normalizedPhone;
    }
}
