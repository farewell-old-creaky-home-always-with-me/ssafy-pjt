package com.ssafy.home.notice.service;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import com.ssafy.home.global.response.PageResponse;
import com.ssafy.home.notice.dto.NoticeDetailResponse;
import com.ssafy.home.notice.dto.NoticeEntity;
import com.ssafy.home.notice.dto.NoticeIdResponse;
import com.ssafy.home.notice.dto.NoticeListItemResponse;
import com.ssafy.home.notice.dto.NoticeRequest;
import com.ssafy.home.notice.mapper.NoticeMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeMapper noticeMapper;

    public PageResponse<NoticeListItemResponse> getNotices(int page, int size) {
        validatePage(page, size);
        long total = noticeMapper.countNotices();
        List<NoticeListItemResponse> items = noticeMapper.findNotices((page - 1) * size, size)
                .stream()
                .map(this::toListItem)
                .toList();
        return new PageResponse<>(items, total, page, size);
    }

    public NoticeDetailResponse getNotice(Long noticeId) {
        return toDetail(requireNotice(noticeId));
    }

    public NoticeIdResponse createNotice(Long memberId, NoticeRequest request) {
        NoticeEntity notice = new NoticeEntity();
        notice.setMemberId(memberId);
        notice.setTitle(request.title().trim());
        notice.setContent(request.content().trim());
        noticeMapper.insertNotice(notice);
        return new NoticeIdResponse(notice.getId());
    }

    public NoticeIdResponse updateNotice(Long noticeId, NoticeRequest request) {
        NoticeEntity notice = requireNotice(noticeId);
        notice.setTitle(request.title().trim());
        notice.setContent(request.content().trim());
        noticeMapper.updateNotice(notice);
        return new NoticeIdResponse(noticeId);
    }

    public void deleteNotice(Long noticeId) {
        requireNotice(noticeId);
        noticeMapper.deleteNoticeById(noticeId);
    }

    private void validatePage(int page, int size) {
        if (page < 1 || size < 1 || size > 100) {
            throw new CustomException(ErrorCode.COMMON_INVALID_PAGE);
        }
    }

    private NoticeEntity requireNotice(Long noticeId) {
        NoticeEntity notice = noticeMapper.findNoticeById(noticeId);
        if (notice == null) {
            throw new CustomException(ErrorCode.NOTICE_NOT_FOUND);
        }
        return notice;
    }

    private NoticeListItemResponse toListItem(NoticeEntity notice) {
        return new NoticeListItemResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getAuthorName(),
                notice.getCreatedAt()
        );
    }

    private NoticeDetailResponse toDetail(NoticeEntity notice) {
        return new NoticeDetailResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getAuthorName(),
                notice.getCreatedAt()
        );
    }
}
