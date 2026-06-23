package com.ssafy.home.notice.controller;

import com.ssafy.home.global.auth.LoginMemberId;
import com.ssafy.home.global.interceptor.AdminOnly;
import com.ssafy.home.notice.dto.NoticeCreateRequest;
import com.ssafy.home.notice.dto.NoticeIdResponse;
import com.ssafy.home.notice.dto.NoticeUpdateRequest;
import com.ssafy.home.notice.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AdminOnly
@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final NoticeService noticeService;

    @PostMapping
    public ResponseEntity<NoticeIdResponse> createNotice(
            @Valid @RequestBody NoticeCreateRequest request,
            @LoginMemberId Long memberId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noticeService.createNotice(memberId, request));
    }

    @PutMapping("/{noticeId}")
    public ResponseEntity<NoticeIdResponse> updateNotice(
            @PathVariable Long noticeId,
            @Valid @RequestBody NoticeUpdateRequest request
    ) {
        return ResponseEntity.ok(noticeService.updateNotice(noticeId, request));
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return ResponseEntity.noContent().build();
    }
}
