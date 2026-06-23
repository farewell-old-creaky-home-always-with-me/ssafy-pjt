package com.ssafy.home.qna.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.qna.mapper.dto.QnaAnswerUpdateParam;
import com.ssafy.home.qna.mapper.dto.QnaResult;
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
    @DisplayName("QnA 답변을 등록하고 수정한다")
    void updateAnswerById() {
        // given
        QnaAnswerUpdateParam param = new QnaAnswerUpdateParam();
        param.setId(1L);
        param.setAnswer("Admin answer");

        // when
        int updated = qnaMapper.updateAnswerById(param);

        // then
        QnaResult qna = qnaMapper.findById(1L);
        assertThat(updated).isEqualTo(1);
        assertThat(qna.getAnswer()).isEqualTo("Admin answer");
        assertThat(qna.getAnsweredAt()).isNotNull();
    }

    @Test
    @DisplayName("QnA 답변을 삭제한다")
    void deleteAnswerById() {
        // when
        int updated = qnaMapper.deleteAnswerById(2L);

        // then
        QnaResult qna = qnaMapper.findById(2L);
        assertThat(updated).isEqualTo(1);
        assertThat(qna.getAnswer()).isNull();
        assertThat(qna.getAnsweredAt()).isNull();
    }
}
