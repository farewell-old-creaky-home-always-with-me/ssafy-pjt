package com.ssafy.home.qna.controller;

import com.ssafy.home.global.auth.LoginMemberId;
import com.ssafy.home.global.response.PageResponse;
import com.ssafy.home.qna.dto.QnaCreateRequest;
import com.ssafy.home.qna.dto.QnaDetailResponse;
import com.ssafy.home.qna.dto.QnaIdResponse;
import com.ssafy.home.qna.dto.QnaListItemResponse;
import com.ssafy.home.qna.dto.QnaStatus;
import com.ssafy.home.qna.dto.QnaUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "QnA", description = "QnA 게시판 API")
public interface QnaApiDocs {

    @Operation(summary = "QnA 목록 조회", description = "QnA 목록을 페이지 단위로 조회합니다.")
    ResponseEntity<PageResponse<QnaListItemResponse>> getQnas(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "답변 상태", example = "WAITING")
            @RequestParam(required = false) QnaStatus status
    );

    @Operation(summary = "QnA 상세 조회", description = "QnA ID로 상세 정보를 조회합니다.")
    ResponseEntity<QnaDetailResponse> getQna(
            @Parameter(description = "QnA ID", example = "1")
            @PathVariable Long qnaId
    );

    @Operation(summary = "QnA 질문 등록", description = "현재 로그인한 회원의 질문을 등록합니다.")
    ResponseEntity<QnaIdResponse> createQna(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "QnA 질문 등록 요청", required = true)
            @Valid @RequestBody QnaCreateRequest request,
            @Parameter(hidden = true) @LoginMemberId Long memberId
    );

    @Operation(summary = "QnA 질문 수정", description = "현재 로그인한 회원이 본인 질문을 수정합니다.")
    ResponseEntity<QnaIdResponse> updateQna(
            @Parameter(description = "QnA ID", example = "1")
            @PathVariable Long qnaId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "QnA 질문 수정 요청", required = true)
            @Valid @RequestBody QnaUpdateRequest request,
            @Parameter(hidden = true) @LoginMemberId Long memberId
    );

    @Operation(summary = "QnA 질문 삭제", description = "현재 로그인한 회원이 본인 질문을 삭제합니다.")
    ResponseEntity<Void> deleteQna(
            @Parameter(description = "QnA ID", example = "1")
            @PathVariable Long qnaId,
            @Parameter(hidden = true) @LoginMemberId Long memberId
    );
}
