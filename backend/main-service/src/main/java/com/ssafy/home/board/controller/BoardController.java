package com.ssafy.home.board.controller;

import com.ssafy.home.board.dto.BoardCreateRequest;
import com.ssafy.home.board.dto.BoardDetailResponse;
import com.ssafy.home.board.dto.BoardIdResponse;
import com.ssafy.home.board.dto.BoardListItemResponse;
import com.ssafy.home.board.dto.BoardUpdateRequest;
import com.ssafy.home.board.service.BoardService;
import com.ssafy.home.global.auth.LoginMemberId;
import com.ssafy.home.global.interceptor.LoginRequired;
import com.ssafy.home.global.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<PageResponse<BoardListItemResponse>> getBoards(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(boardService.getBoards(page, size));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDetailResponse> getBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.getBoard(boardId));
    }

    @LoginRequired
    @PostMapping
    public ResponseEntity<BoardIdResponse> createBoard(
            @Valid @RequestBody BoardCreateRequest request,
            @LoginMemberId Long memberId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardService.createBoard(memberId, request));
    }

    @LoginRequired
    @PutMapping("/{boardId}")
    public ResponseEntity<BoardIdResponse> updateBoard(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardUpdateRequest request,
            @LoginMemberId Long memberId
    ) {
        return ResponseEntity.ok(boardService.updateBoard(memberId, boardId, request));
    }

    @LoginRequired
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId, @LoginMemberId Long memberId) {
        boardService.deleteBoard(memberId, boardId);
        return ResponseEntity.noContent().build();
    }
}
