package com.ssafy.home.notice.service;

import com.ssafy.home.global.exception.CustomException;
import static com.ssafy.home.global.exception.ErrorCode.COMMON_INVALID_PAGE;
import static com.ssafy.home.global.exception.ErrorCode.NOTICE_NOT_FOUND;
import lombok.RequiredArgsConstructor;
import com.ssafy.home.global.response.PageResponse;
import com.ssafy.home.notice.dto.NoticeDetailResponse;
import com.ssafy.home.notice.mapper.dto.NoticeCreateParam;
import com.ssafy.home.notice.mapper.dto.NoticeUpdateParam;
import com.ssafy.home.notice.mapper.dto.NoticeResult;
import com.ssafy.home.notice.dto.NoticeIdResponse;
import com.ssafy.home.notice.dto.NoticeListItemResponse;
import com.ssafy.home.notice.dto.NoticeCreateRequest;
import com.ssafy.home.notice.dto.NoticeUpdateRequest;
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

    @Transactional
    public NoticeIdResponse createNotice(Long memberId, NoticeCreateRequest request) {
        NoticeCreateParam notice = new NoticeCreateParam();
        notice.setMemberId(memberId);
        notice.setTitle(request.title().trim());
        notice.setContent(request.content().trim());
        noticeMapper.insert(notice);
        return NoticeIdResponse.of(notice.getId());
    }

    @Transactional
    public NoticeIdResponse updateNotice(Long noticeId, NoticeUpdateRequest request) {
        NoticeResult notice = requireNotice(noticeId);
        NoticeUpdateParam param = new NoticeUpdateParam();
        param.setId(notice.getId());
        param.setTitle(request.title().trim());
        param.setContent(request.content().trim());
        noticeMapper.updateById(param);
        return NoticeIdResponse.of(noticeId);
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        requireNotice(noticeId);
        noticeMapper.deleteById(noticeId);
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
