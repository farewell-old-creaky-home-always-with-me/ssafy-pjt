package com.ssafy.home.qna.mapper;

import com.ssafy.home.qna.mapper.dto.QnaAnswerUpdateParam;
import com.ssafy.home.qna.mapper.dto.QnaResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QnaMapper {

    QnaResult findById(@Param("qnaId") Long qnaId);

    int updateAnswerById(QnaAnswerUpdateParam qna);

    int deleteAnswerById(@Param("qnaId") Long qnaId);
}
