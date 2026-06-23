package com.ssafy.home.notice.service;

import com.ssafy.home.global.exception.CustomException;
import static com.ssafy.home.global.exception.ErrorCode.COMMON_INVALID_PAGE;
import static com.ssafy.home.global.exception.ErrorCode.NOTICE_NOT_FOUND;
import lombok.RequiredArgsConstructor;
import com.ssafy.home.global.response.PageResponse;
import com.ssafy.home.notice.dto.NoticeDetailResponse;
import com.ssafy.home.notice.mapper.dto.NoticeResult;
import com.ssafy.home.notice.dto.NoticeListItemResponse;
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
        long total = noticeMapper.countAll();
        List<NoticeListItemResponse> items = noticeMapper.findAll((page - 1) * size, size)
                .stream()
                .map(NoticeListItemResponse::from)
                .toList();
        return PageResponse.of(items, total, page, size);
    }

    @Transactional(readOnly = true)
    public NoticeDetailResponse getNotice(Long noticeId) {
        return NoticeDetailResponse.from(requireNotice(noticeId));
    }

    private void validatePage(int page, int size) {
        if (page < 1 || size < 1 || size > 100) {
            throw new CustomException(COMMON_INVALID_PAGE);
        }
    }

    private NoticeResult requireNotice(Long noticeId) {
        NoticeResult notice = noticeMapper.findById(noticeId);
        if (notice == null) {
            throw new CustomException(NOTICE_NOT_FOUND);
        }
        return notice;
    }
}
