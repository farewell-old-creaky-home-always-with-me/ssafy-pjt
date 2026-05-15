package com.ssafy.home.favorite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFavoriteRequest(
        @NotBlank(message = "행정구역 코드는 필수입니다")
        @Size(min = 10, max = 10, message = "행정구역 코드는 10자리여야 합니다")
        String regionCode
) {
}
