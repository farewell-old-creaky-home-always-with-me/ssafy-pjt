package com.ssafy.home.board.dto;

import com.ssafy.home.board.mapper.dto.BoardResult;
import java.time.LocalDateTime;

public record BoardListItemResponse(
        Long boardId,
        Long memberId,
        String title,
        String authorName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static BoardListItemResponse from(BoardResult board) {
        return new BoardListItemResponse(
                board.getId(),
                board.getMemberId(),
                board.getTitle(),
                board.getAuthorName(),
                board.getCreatedAt(),
                board.getUpdatedAt()
        );
    }
}
