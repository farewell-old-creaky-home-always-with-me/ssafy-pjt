package com.ssafy.home.qna.controller;

import com.ssafy.home.global.interceptor.AdminOnly;
import com.ssafy.home.qna.dto.QnaAnswerRequest;
import com.ssafy.home.qna.dto.QnaIdResponse;
import com.ssafy.home.qna.service.QnaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AdminOnly
@RestController
@RequestMapping("/api/qnas")
@RequiredArgsConstructor
public class AdminQnaController {

    private final QnaService qnaService;

    @PutMapping("/{qnaId}/answer")
    public ResponseEntity<QnaIdResponse> updateAnswer(
            @PathVariable Long qnaId,
            @Valid @RequestBody QnaAnswerRequest request
    ) {
        return ResponseEntity.ok(qnaService.updateAnswer(qnaId, request));
    }

    @DeleteMapping("/{qnaId}/answer")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long qnaId) {
        qnaService.deleteAnswer(qnaId);
        return ResponseEntity.noContent().build();
    }
}
