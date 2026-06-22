package com.ssafy.home.batch.domain;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HouseType {
    APARTMENT("아파트"),
    MULTI_FAMILY("다세대");

    private final String name;

    public static HouseType from(String value) {
        try {
            return HouseType.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new CustomException(ErrorCode.BATCH_INVALID_PARAMETER);
        }
    }
}
