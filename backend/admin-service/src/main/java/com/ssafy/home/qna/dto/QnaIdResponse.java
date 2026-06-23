package com.ssafy.home.qna.dto;

public record QnaIdResponse(Long qnaId) {

    public static QnaIdResponse of(Long qnaId) {
        return new QnaIdResponse(qnaId);
    }
}
