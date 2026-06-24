package com.ssafy.home.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record HouseDealCollectRequest(
        @Pattern(
                regexp = "^(|\\d{5}|\\d{10})$",
                message = "행정구역 코드는 비워두거나 5자리 또는 10자리 숫자여야 합니다"
        )
        String regionCode,
        @NotBlank(message = "기준 연월은 필수입니다")
        @Pattern(regexp = "\\d{6}", message = "기준 연월은 6자리 숫자여야 합니다")
        String yearMonth,
        @NotBlank(message = "주택 유형은 필수입니다")
        String houseType,
        @NotBlank(message = "거래 유형은 필수입니다")
        String dealType
) {
}
