package com.ssafy.home.qna.controller;

import com.ssafy.home.global.auth.LoginMemberId;
import com.ssafy.home.global.interceptor.LoginRequired;
import com.ssafy.home.global.response.PageResponse;
import com.ssafy.home.qna.dto.QnaCreateRequest;
import com.ssafy.home.qna.dto.QnaDetailResponse;
import com.ssafy.home.qna.dto.QnaIdResponse;
import com.ssafy.home.qna.dto.QnaListItemResponse;
import com.ssafy.home.qna.dto.QnaStatus;
import com.ssafy.home.qna.dto.QnaUpdateRequest;
import com.ssafy.home.qna.service.QnaService;
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
@RequestMapping("/api/qnas")
@RequiredArgsConstructor
public class QnaController implements QnaApiDocs {

    private final QnaService qnaService;

    @GetMapping
    @Override
    public ResponseEntity<PageResponse<QnaListItemResponse>> getQnas(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) QnaStatus status
    ) {
        return ResponseEntity.ok(qnaService.getQnas(page, size, status));
    }

    @GetMapping("/{qnaId}")
    @Override
    public ResponseEntity<QnaDetailResponse> getQna(@PathVariable Long qnaId) {
        return ResponseEntity.ok(qnaService.getQna(qnaId));
    }

    @LoginRequired
    @PostMapping
    @Override
    public ResponseEntity<QnaIdResponse> createQna(
            @Valid @RequestBody QnaCreateRequest request,
            @LoginMemberId Long memberId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(qnaService.createQna(memberId, request));
    }

    @LoginRequired
    @PutMapping("/{qnaId}")
    @Override
    public ResponseEntity<QnaIdResponse> updateQna(
            @PathVariable Long qnaId,
            @Valid @RequestBody QnaUpdateRequest request,
            @LoginMemberId Long memberId
    ) {
        return ResponseEntity.ok(qnaService.updateQna(memberId, qnaId, request));
    }

    @LoginRequired
    @DeleteMapping("/{qnaId}")
    @Override
    public ResponseEntity<Void> deleteQna(@PathVariable Long qnaId, @LoginMemberId Long memberId) {
        qnaService.deleteQna(memberId, qnaId);
        return ResponseEntity.noContent().build();
    }
}
