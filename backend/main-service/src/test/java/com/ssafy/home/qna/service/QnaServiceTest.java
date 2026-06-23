package com.ssafy.home.qna.service;

import static com.ssafy.home.global.exception.ErrorCode.COMMON_INVALID_PAGE;
import static com.ssafy.home.global.exception.ErrorCode.QNA_FORBIDDEN;
import static com.ssafy.home.global.exception.ErrorCode.QNA_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.qna.dto.QnaCreateRequest;
import com.ssafy.home.qna.dto.QnaStatus;
import com.ssafy.home.qna.dto.QnaUpdateRequest;
import com.ssafy.home.qna.mapper.QnaMapper;
import com.ssafy.home.qna.mapper.dto.QnaResult;
import java.time.LocalDateTime;
import java.util.List;
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
    @DisplayName("페이지 조건이 올바르지 않으면 예외가 발생한다")
    void getQnasThrowsWhenPageInvalid() {
        // when / then
        assertThatThrownBy(() -> qnaService.getQnas(0, 20, null))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(COMMON_INVALID_PAGE));
    }

    @Test
    @DisplayName("페이지 크기가 0이면 예외가 발생한다")
    void getQnasThrowsWhenSizeIsZero() {
        // when / then
        assertThatThrownBy(() -> qnaService.getQnas(1, 0, null))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(COMMON_INVALID_PAGE));
    }

    @Test
    @DisplayName("페이지 크기가 100을 초과하면 예외가 발생한다")
    void getQnasThrowsWhenSizeExceedsMax() {
        // when / then
        assertThatThrownBy(() -> qnaService.getQnas(1, 101, null))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(COMMON_INVALID_PAGE));
    }

    @Test
    @DisplayName("QnA 목록을 상태별로 조회한다")
    void getQnasReturnsPage() {
        // given
        given(qnaMapper.countAll(QnaStatus.WAITING)).willReturn(1L);
        given(qnaMapper.findAll(0, 20, QnaStatus.WAITING)).willReturn(List.of(qnaResult(1L, 1L)));

        // when
        var result = qnaService.getQnas(1, 20, QnaStatus.WAITING);

        // then
        assertThat(result.total()).isEqualTo(1);
        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).status()).isEqualTo(QnaStatus.WAITING);
    }

    @Test
    @DisplayName("존재하지 않는 QnA를 조회하면 예외가 발생한다")
    void getQnaThrowsWhenNotFound() {
        // given
        given(qnaMapper.findById(99L)).willReturn(null);

        // when / then
        assertThatThrownBy(() -> qnaService.getQna(99L))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(QNA_NOT_FOUND));
    }

    @Test
    @DisplayName("QnA를 등록하면 제목과 내용을 trim해 저장한다")
    void createQnaTrimsRequest() {
        // when
        qnaService.createQna(1L, new QnaCreateRequest("  제목  ", "  내용  "));

        // then
        then(qnaMapper).should().insert(org.mockito.ArgumentMatchers.argThat(param ->
                param.getMemberId().equals(1L)
                        && param.getTitle().equals("제목")
                        && param.getContent().equals("내용")
        ));
    }

    @Test
    @DisplayName("작성자가 아니면 QnA를 수정할 수 없다")
    void updateQnaThrowsWhenOwnerDiffers() {
        // given
        given(qnaMapper.findById(1L)).willReturn(qnaResult(1L, 2L));

        // when / then
        assertThatThrownBy(() -> qnaService.updateQna(1L, 1L, new QnaUpdateRequest("제목", "내용")))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(QNA_FORBIDDEN));
    }

    @Test
    @DisplayName("작성자가 아니면 QnA를 삭제할 수 없다")
    void deleteQnaThrowsWhenOwnerDiffers() {
        // given
        given(qnaMapper.findById(1L)).willReturn(qnaResult(1L, 2L));

        // when / then
        assertThatThrownBy(() -> qnaService.deleteQna(1L, 1L))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(QNA_FORBIDDEN));
    }

    private QnaResult qnaResult(Long qnaId, Long memberId) {
        QnaResult qna = new QnaResult();
        qna.setId(qnaId);
        qna.setMemberId(memberId);
        qna.setTitle("질문");
        qna.setContent("질문 내용");
        qna.setAuthorName("홍길동");
        qna.setCreatedAt(LocalDateTime.of(2026, 6, 1, 10, 0));
        return qna;
    }
}
