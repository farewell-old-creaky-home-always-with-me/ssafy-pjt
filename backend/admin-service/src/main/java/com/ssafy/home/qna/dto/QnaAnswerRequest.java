package com.ssafy.home.qna.dto;

import jakarta.validation.constraints.NotBlank;

public record QnaAnswerRequest(
        @NotBlank(message = "답변은 필수입니다")
        String answer
) {
}
