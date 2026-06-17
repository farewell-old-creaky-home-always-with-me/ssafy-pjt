package com.ssafy.home.notice.controller;

import com.ssafy.home.global.auth.LoginMemberId;
import com.ssafy.home.global.interceptor.AdminOnly;
import com.ssafy.home.global.response.PageResponse;
import com.ssafy.home.notice.dto.NoticeDetailResponse;
import com.ssafy.home.notice.dto.NoticeIdResponse;
import com.ssafy.home.notice.dto.NoticeListItemResponse;
import com.ssafy.home.notice.dto.NoticeRequest;
import com.ssafy.home.notice.service.NoticeService;
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
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController implements NoticeApiDocs {

    private final NoticeService noticeService;

    @GetMapping
    @Override
    public ResponseEntity<PageResponse<NoticeListItemResponse>> getNotices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(noticeService.getNotices(page, size));
    }

    @GetMapping("/{noticeId}")
    @Override
    public ResponseEntity<NoticeDetailResponse> getNotice(@PathVariable Long noticeId) {
        return ResponseEntity.ok(noticeService.getNotice(noticeId));
    }

    @AdminOnly
    @PostMapping
    @Override
    public ResponseEntity<NoticeIdResponse> createNotice(
            @Valid @RequestBody NoticeRequest request,
            @LoginMemberId Long memberId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noticeService.createNotice(memberId, request));
    }

    @AdminOnly
    @PutMapping("/{noticeId}")
    @Override
    public ResponseEntity<NoticeIdResponse> updateNotice(
            @PathVariable Long noticeId,
            @Valid @RequestBody NoticeRequest request
    ) {
        return ResponseEntity.ok(noticeService.updateNotice(noticeId, request));
    }

    @AdminOnly
    @DeleteMapping("/{noticeId}")
    @Override
    public ResponseEntity<Void> deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return ResponseEntity.noContent().build();
    }
}
