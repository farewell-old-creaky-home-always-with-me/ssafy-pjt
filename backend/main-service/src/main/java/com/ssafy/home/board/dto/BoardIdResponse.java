package com.ssafy.home.board.dto;

public record BoardIdResponse(Long boardId) {

    public static BoardIdResponse of(Long boardId) {
        return new BoardIdResponse(boardId);
    }
}
