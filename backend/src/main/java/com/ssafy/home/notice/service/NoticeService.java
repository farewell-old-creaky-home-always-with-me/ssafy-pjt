package com.ssafy.home.notice.service;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import com.ssafy.home.global.response.PageResponse;
import com.ssafy.home.notice.dto.NoticeDetailResponse;
import com.ssafy.home.notice.mapper.dto.NoticeParam;
import com.ssafy.home.notice.mapper.dto.NoticeResult;
import com.ssafy.home.notice.dto.NoticeIdResponse;
import com.ssafy.home.notice.dto.NoticeListItemResponse;
import com.ssafy.home.notice.dto.NoticeRequest;
import com.ssafy.home.notice.mapper.NoticeMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeMapper noticeMapper;

    @Transactional(readOnly = true)
    public PageResponse<NoticeListItemResponse> getNotices(int page, int size) {
        validatePage(page, size);
        long total = noticeMapper.count();
        List<NoticeListItemResponse> items = noticeMapper.findAll((page - 1) * size, size)
                .stream()
                .map(this::toListItem)
                .toList();
        return new PageResponse<>(items, total, page, size);
    }

    @Transactional(readOnly = true)
    public NoticeDetailResponse getNotice(Long noticeId) {
        return toDetail(requireNotice(noticeId));
    }

    @Transactional
    public NoticeIdResponse createNotice(Long memberId, NoticeRequest request) {
        NoticeParam notice = new NoticeParam();
        notice.setMemberId(memberId);
        notice.setTitle(request.title().trim());
        notice.setContent(request.content().trim());
        noticeMapper.insert(notice);
        return new NoticeIdResponse(notice.getId());
    }

    @Transactional
    public NoticeIdResponse updateNotice(Long noticeId, NoticeRequest request) {
        NoticeResult notice = requireNotice(noticeId);
        NoticeParam param = new NoticeParam();
        param.setId(notice.getId());
        param.setTitle(request.title().trim());
        param.setContent(request.content().trim());
        noticeMapper.update(param);
        return new NoticeIdResponse(noticeId);
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        requireNotice(noticeId);
        noticeMapper.delete(noticeId);
    }

    private void validatePage(int page, int size) {
        if (page < 1 || size < 1 || size > 100) {
            throw new CustomException(ErrorCode.COMMON_INVALID_PAGE);
        }
    }

    private NoticeResult requireNotice(Long noticeId) {
        NoticeResult notice = noticeMapper.findById(noticeId);
        if (notice == null) {
            throw new CustomException(ErrorCode.NOTICE_NOT_FOUND);
        }
        return notice;
    }

    private NoticeListItemResponse toListItem(NoticeResult notice) {
        return new NoticeListItemResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getAuthorName(),
                notice.getCreatedAt()
        );
    }

    private NoticeDetailResponse toDetail(NoticeResult notice) {
        return new NoticeDetailResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getAuthorName(),
                notice.getCreatedAt()
        );
    }
}
