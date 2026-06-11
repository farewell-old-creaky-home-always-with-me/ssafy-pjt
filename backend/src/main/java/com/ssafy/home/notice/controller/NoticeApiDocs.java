package com.ssafy.home.notice.controller;

import com.ssafy.home.global.auth.LoginMemberId;
import com.ssafy.home.global.response.PageResponse;
import com.ssafy.home.notice.dto.NoticeDetailResponse;
import com.ssafy.home.notice.dto.NoticeIdResponse;
import com.ssafy.home.notice.dto.NoticeListItemResponse;
import com.ssafy.home.notice.dto.NoticeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Notice", description = "공지사항 API")
public interface NoticeApiDocs {

    @Operation(
            summary = "공지사항 목록 조회",
            description = "공지사항 목록을 페이지 단위로 조회합니다."
    )
    PageResponse<NoticeListItemResponse> getNotices(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size
    );

    @Operation(
            summary = "공지사항 상세 조회",
            description = "공지사항 ID로 상세 정보를 조회합니다."
    )
    NoticeDetailResponse getNotice(
            @Parameter(description = "공지사항 ID", example = "1")
            @PathVariable Long noticeId
    );

    @Operation(
            summary = "공지사항 등록",
            description = "관리자가 공지사항을 등록합니다."
    )
    ResponseEntity<NoticeIdResponse> createNotice(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "공지사항 요청", required = true)
            @Valid @RequestBody NoticeRequest request,
            @Parameter(hidden = true) @LoginMemberId Long memberId
    );

    @Operation(
            summary = "공지사항 수정",
            description = "관리자가 공지사항을 수정합니다."
    )
    NoticeIdResponse updateNotice(
            @Parameter(description = "공지사항 ID", example = "1")
            @PathVariable Long noticeId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "공지사항 요청", required = true)
            @Valid @RequestBody NoticeRequest request
    );

    @Operation(
            summary = "공지사항 삭제",
            description = "관리자가 공지사항을 삭제합니다."
    )
    ResponseEntity<Void> deleteNotice(
            @Parameter(description = "공지사항 ID", example = "1")
            @PathVariable Long noticeId
    );
}
