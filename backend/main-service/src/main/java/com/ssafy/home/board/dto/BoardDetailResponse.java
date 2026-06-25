package com.ssafy.home.board.dto;

import com.ssafy.home.board.mapper.dto.BoardResult;
import java.time.LocalDateTime;

public record BoardDetailResponse(
        Long boardId,
        Long memberId,
        String title,
        String content,
        String authorName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static BoardDetailResponse from(BoardResult board) {
        return new BoardDetailResponse(
                board.getId(),
                board.getMemberId(),
                board.getTitle(),
                board.getContent(),
                board.getAuthorName(),
                board.getCreatedAt(),
                board.getUpdatedAt()
        );
    }
}
