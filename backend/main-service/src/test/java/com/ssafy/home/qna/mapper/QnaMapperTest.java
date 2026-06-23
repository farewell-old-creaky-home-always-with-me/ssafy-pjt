package com.ssafy.home.qna.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.qna.dto.QnaStatus;
import com.ssafy.home.qna.mapper.dto.QnaCreateParam;
import com.ssafy.home.qna.mapper.dto.QnaResult;
import com.ssafy.home.qna.mapper.dto.QnaUpdateParam;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@MybatisTest
@Sql("/sql/qna-data.sql")
class QnaMapperTest {

    @Autowired
    private QnaMapper qnaMapper;

    @Test
    @DisplayName("QnA 목록을 상태별로 조회한다")
    void findAllByStatus() {
        // when
        List<QnaResult> waiting = qnaMapper.findAll(0, 20, QnaStatus.WAITING);
        List<QnaResult> answered = qnaMapper.findAll(0, 20, QnaStatus.ANSWERED);

        // then
        assertThat(waiting).hasSize(1);
        assertThat(waiting.get(0).getAnsweredAt()).isNull();
        assertThat(answered).hasSize(1);
        assertThat(answered.get(0).getAnswer()).isEqualTo("Admin answer");
    }

    @Test
    @DisplayName("QnA를 상세 조회한다")
    void findById() {
        // when
        QnaResult qna = qnaMapper.findById(1L);

        // then
        assertThat(qna.getTitle()).isEqualTo("First question");
        assertThat(qna.getAuthorName()).isEqualTo("User One");
    }

    @Test
    @DisplayName("QnA를 등록하고 수정·삭제한다")
    void insertUpdateAndDelete() {
        // given
        QnaCreateParam createParam = new QnaCreateParam();
        createParam.setMemberId(1L);
        createParam.setTitle("새 질문");
        createParam.setContent("새 질문 내용");

        // when
        qnaMapper.insert(createParam);

        // then
        assertThat(createParam.getId()).isNotNull();

        // given
        QnaUpdateParam updateParam = new QnaUpdateParam();
        updateParam.setId(createParam.getId());
        updateParam.setTitle("수정 질문");
        updateParam.setContent("수정 질문 내용");

        // when
        int updated = qnaMapper.updateById(updateParam);

        // then
        assertThat(updated).isEqualTo(1);
        assertThat(qnaMapper.findById(createParam.getId()).getTitle()).isEqualTo("수정 질문");

        // when
        int deleted = qnaMapper.deleteById(createParam.getId());

        // then
        assertThat(deleted).isEqualTo(1);
        assertThat(qnaMapper.findById(createParam.getId())).isNull();
    }
}
