package com.ssafy.home.qna.dto;

import com.ssafy.home.qna.mapper.dto.QnaResult;
import java.time.LocalDateTime;

public record QnaDetailResponse(
        Long qnaId,
        Long memberId,
        String title,
        String content,
        String authorName,
        String answer,
        QnaStatus status,
        LocalDateTime answeredAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static QnaDetailResponse from(QnaResult qna) {
        return new QnaDetailResponse(
                qna.getId(),
                qna.getMemberId(),
                qna.getTitle(),
                qna.getContent(),
                qna.getAuthorName(),
                qna.getAnswer(),
                QnaStatusResolver.resolve(qna.getAnsweredAt()),
                qna.getAnsweredAt(),
                qna.getCreatedAt(),
                qna.getUpdatedAt()
        );
    }
}
