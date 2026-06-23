package com.ssafy.home.qna.service;

import static com.ssafy.home.global.exception.ErrorCode.QNA_ANSWER_NOT_FOUND;
import static com.ssafy.home.global.exception.ErrorCode.QNA_NOT_FOUND;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.qna.dto.QnaAnswerRequest;
import com.ssafy.home.qna.dto.QnaIdResponse;
import com.ssafy.home.qna.mapper.QnaMapper;
import com.ssafy.home.qna.mapper.dto.QnaAnswerUpdateParam;
import com.ssafy.home.qna.mapper.dto.QnaResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QnaService {

    private final QnaMapper qnaMapper;

    @Transactional
    public QnaIdResponse updateAnswer(Long qnaId, QnaAnswerRequest request) {
        QnaResult qna = requireQna(qnaId);
        QnaAnswerUpdateParam param = new QnaAnswerUpdateParam();
        param.setId(qna.getId());
        param.setAnswer(request.answer().trim());
        qnaMapper.updateAnswerById(param);
        return QnaIdResponse.of(qnaId);
    }

    @Transactional
    public void deleteAnswer(Long qnaId) {
        QnaResult qna = requireQna(qnaId);
        if (qna.getAnsweredAt() == null) {
            throw new CustomException(QNA_ANSWER_NOT_FOUND);
        }
        qnaMapper.deleteAnswerById(qnaId);
    }

    private QnaResult requireQna(Long qnaId) {
        QnaResult qna = qnaMapper.findById(qnaId);
        if (qna == null) {
            throw new CustomException(QNA_NOT_FOUND);
        }
        return qna;
    }
}
