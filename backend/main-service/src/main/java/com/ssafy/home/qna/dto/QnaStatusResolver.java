package com.ssafy.home.qna.dto;

import java.time.LocalDateTime;

final class QnaStatusResolver {

    private QnaStatusResolver() {
    }

    static QnaStatus resolve(LocalDateTime answeredAt) {
        return answeredAt == null ? QnaStatus.WAITING : QnaStatus.ANSWERED;
    }
}
