package com.ssafy.home.qna.service;

import static com.ssafy.home.global.exception.ErrorCode.QNA_ANSWER_NOT_FOUND;
import static com.ssafy.home.global.exception.ErrorCode.QNA_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.qna.dto.QnaAnswerRequest;
import com.ssafy.home.qna.mapper.QnaMapper;
import com.ssafy.home.qna.mapper.dto.QnaResult;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QnaServiceTest {

    @Mock
    private QnaMapper qnaMapper;

    private QnaService qnaService;

    @BeforeEach
    void setUp() {
        qnaService = new QnaService(qnaMapper);
    }

    @Test
    @DisplayName("존재하지 않는 QnA에 답변하면 예외가 발생한다")
    void updateAnswerThrowsWhenQnaNotFound() {
        // given
        given(qnaMapper.findById(99L)).willReturn(null);

        // when / then
        assertThatThrownBy(() -> qnaService.updateAnswer(99L, new QnaAnswerRequest("답변")))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(QNA_NOT_FOUND));
    }

    @Test
    @DisplayName("답변을 trim해 저장한다")
    void updateAnswerTrimsRequest() {
        // given
        given(qnaMapper.findById(1L)).willReturn(qnaResult(null));
        given(qnaMapper.updateAnswerById(org.mockito.ArgumentMatchers.any())).willReturn(1);

        // when
        var result = qnaService.updateAnswer(1L, new QnaAnswerRequest("  답변  "));

        // then
        assertThat(result.qnaId()).isEqualTo(1L);
        then(qnaMapper).should().updateAnswerById(org.mockito.ArgumentMatchers.argThat(param ->
                param.getId().equals(1L) && param.getAnswer().equals("답변")
        ));
    }

    @Test
    @DisplayName("답변 저장 영향 row가 없으면 예외가 발생한다")
    void updateAnswerThrowsWhenNoRowsUpdated() {
        // given
        given(qnaMapper.findById(1L)).willReturn(qnaResult(null));
        given(qnaMapper.updateAnswerById(org.mockito.ArgumentMatchers.any())).willReturn(0);

        // when / then
        assertThatThrownBy(() -> qnaService.updateAnswer(1L, new QnaAnswerRequest("답변")))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(QNA_NOT_FOUND));
    }

    @Test
    @DisplayName("답변이 없으면 답변 삭제 시 예외가 발생한다")
    void deleteAnswerThrowsWhenAnswerNotFound() {
        // given
        given(qnaMapper.findById(1L)).willReturn(qnaResult(null));

        // when / then
        assertThatThrownBy(() -> qnaService.deleteAnswer(1L))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(QNA_ANSWER_NOT_FOUND));
    }

    @Test
    @DisplayName("답변이 있으면 삭제한다")
    void deleteAnswerDeletesWhenAnswered() {
        // given
        given(qnaMapper.findById(1L)).willReturn(qnaResult(LocalDateTime.of(2026, 6, 1, 10, 0)));
        given(qnaMapper.deleteAnswerById(1L)).willReturn(1);

        // when
        qnaService.deleteAnswer(1L);

        // then
        then(qnaMapper).should().deleteAnswerById(1L);
    }

    @Test
    @DisplayName("답변 삭제 영향 row가 없으면 예외가 발생한다")
    void deleteAnswerThrowsWhenNoRowsUpdated() {
        // given
        given(qnaMapper.findById(1L)).willReturn(qnaResult(LocalDateTime.of(2026, 6, 1, 10, 0)));
        given(qnaMapper.deleteAnswerById(1L)).willReturn(0);

        // when / then
        assertThatThrownBy(() -> qnaService.deleteAnswer(1L))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(QNA_NOT_FOUND));
    }

    private QnaResult qnaResult(LocalDateTime answeredAt) {
        QnaResult qna = new QnaResult();
        qna.setId(1L);
        qna.setMemberId(1L);
        qna.setTitle("질문");
        qna.setContent("질문 내용");
        qna.setAnsweredAt(answeredAt);
        return qna;
    }
}
