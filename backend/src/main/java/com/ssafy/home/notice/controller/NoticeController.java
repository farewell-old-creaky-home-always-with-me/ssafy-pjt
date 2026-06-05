package com.ssafy.home.notice.controller;

import com.ssafy.home.global.interceptor.AdminOnly;
import com.ssafy.home.global.response.PageResponse;
import com.ssafy.home.notice.dto.NoticeDetailResponse;
import com.ssafy.home.notice.dto.NoticeIdResponse;
import com.ssafy.home.notice.dto.NoticeListItemResponse;
import com.ssafy.home.notice.dto.NoticeRequest;
import com.ssafy.home.notice.service.NoticeService;
import jakarta.servlet.http.HttpSession;
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
    public PageResponse<NoticeListItemResponse> getNotices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return noticeService.getNotices(page, size);
    }

    @GetMapping("/{noticeId}")
    @Override
    public NoticeDetailResponse getNotice(@PathVariable Long noticeId) {
        return noticeService.getNotice(noticeId);
    }

    @AdminOnly
    @PostMapping
    @Override
    public ResponseEntity<NoticeIdResponse> createNotice(
            @Valid @RequestBody NoticeRequest request,
            HttpSession session
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noticeService.createNotice(getMemberId(session), request));
    }

    @AdminOnly
    @PutMapping("/{noticeId}")
    @Override
    public NoticeIdResponse updateNotice(
            @PathVariable Long noticeId,
            @Valid @RequestBody NoticeRequest request
    ) {
        return noticeService.updateNotice(noticeId, request);
    }

    @AdminOnly
    @DeleteMapping("/{noticeId}")
    @Override
    public ResponseEntity<Void> deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return ResponseEntity.noContent().build();
    }

    private Long getMemberId(HttpSession session) {
        return (Long) session.getAttribute("memberId");
    }
}
