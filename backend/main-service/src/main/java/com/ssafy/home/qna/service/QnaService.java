package com.ssafy.home.qna.service;

import static com.ssafy.home.global.exception.ErrorCode.COMMON_INVALID_PAGE;
import static com.ssafy.home.global.exception.ErrorCode.QNA_FORBIDDEN;
import static com.ssafy.home.global.exception.ErrorCode.QNA_NOT_FOUND;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.response.PageResponse;
import com.ssafy.home.qna.dto.QnaCreateRequest;
import com.ssafy.home.qna.dto.QnaDetailResponse;
import com.ssafy.home.qna.dto.QnaIdResponse;
import com.ssafy.home.qna.dto.QnaListItemResponse;
import com.ssafy.home.qna.dto.QnaStatus;
import com.ssafy.home.qna.dto.QnaUpdateRequest;
import com.ssafy.home.qna.mapper.QnaMapper;
import com.ssafy.home.qna.mapper.dto.QnaCreateParam;
import com.ssafy.home.qna.mapper.dto.QnaResult;
import com.ssafy.home.qna.mapper.dto.QnaUpdateParam;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QnaService {

    private final QnaMapper qnaMapper;

    @Transactional(readOnly = true)
    public PageResponse<QnaListItemResponse> getQnas(int page, int size, QnaStatus status) {
        validatePage(page, size);
        long total = qnaMapper.countAll(status);
        List<QnaListItemResponse> items = qnaMapper.findAll((page - 1) * size, size, status)
                .stream()
                .map(QnaListItemResponse::from)
                .toList();
        return PageResponse.of(items, total, page, size);
    }

    @Transactional(readOnly = true)
    public QnaDetailResponse getQna(Long qnaId) {
        return QnaDetailResponse.from(requireQna(qnaId));
    }

    @Transactional
    public QnaIdResponse createQna(Long memberId, QnaCreateRequest request) {
        QnaCreateParam qna = new QnaCreateParam();
        qna.setMemberId(memberId);
        qna.setTitle(request.title().trim());
        qna.setContent(request.content().trim());
        qnaMapper.insert(qna);
        return QnaIdResponse.of(qna.getId());
    }

    @Transactional
    public QnaIdResponse updateQna(Long memberId, Long qnaId, QnaUpdateRequest request) {
        QnaResult existing = requireOwnedQna(memberId, qnaId);
        QnaUpdateParam qna = new QnaUpdateParam();
        qna.setId(existing.getId());
        qna.setTitle(request.title().trim());
        qna.setContent(request.content().trim());
        qnaMapper.updateById(qna);
        return QnaIdResponse.of(qnaId);
    }

    @Transactional
    public void deleteQna(Long memberId, Long qnaId) {
        requireOwnedQna(memberId, qnaId);
        qnaMapper.deleteById(qnaId);
    }

    private void validatePage(int page, int size) {
        if (page < 1 || size < 1 || size > 100) {
            throw new CustomException(COMMON_INVALID_PAGE);
        }
    }

    private QnaResult requireQna(Long qnaId) {
        QnaResult qna = qnaMapper.findById(qnaId);
        if (qna == null) {
            throw new CustomException(QNA_NOT_FOUND);
        }
        return qna;
    }

    private QnaResult requireOwnedQna(Long memberId, Long qnaId) {
        QnaResult qna = requireQna(qnaId);
        if (!memberId.equals(qna.getMemberId())) {
            throw new CustomException(QNA_FORBIDDEN);
        }
        return qna;
    }
}
