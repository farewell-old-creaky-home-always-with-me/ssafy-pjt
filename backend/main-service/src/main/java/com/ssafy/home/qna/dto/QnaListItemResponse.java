package com.ssafy.home.qna.dto;

import com.ssafy.home.qna.mapper.dto.QnaResult;
import java.time.LocalDateTime;

public record QnaListItemResponse(
        Long qnaId,
        String title,
        String authorName,
        QnaStatus status,
        LocalDateTime createdAt,
        LocalDateTime answeredAt
) {

    public static QnaListItemResponse from(QnaResult qna) {
        return new QnaListItemResponse(
                qna.getId(),
                qna.getTitle(),
                qna.getAuthorName(),
                QnaStatusResolver.resolve(qna.getAnsweredAt()),
                qna.getCreatedAt(),
                qna.getAnsweredAt()
        );
    }
}
