package com.ssafy.home.notice.controller;

import com.ssafy.home.global.response.PageResponse;
import com.ssafy.home.notice.dto.NoticeDetailResponse;
import com.ssafy.home.notice.dto.NoticeListItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Notice", description = "공지사항 API")
public interface NoticeApiDocs {

    @Operation(
            summary = "공지사항 목록 조회",
            description = "공지사항 목록을 페이지 단위로 조회합니다."
    )
    ResponseEntity<PageResponse<NoticeListItemResponse>> getNotices(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size
    );

    @Operation(
            summary = "공지사항 상세 조회",
            description = "공지사항 ID로 상세 정보를 조회합니다."
    )
    ResponseEntity<NoticeDetailResponse> getNotice(
            @Parameter(description = "공지사항 ID", example = "1")
            @PathVariable Long noticeId
    );
}
