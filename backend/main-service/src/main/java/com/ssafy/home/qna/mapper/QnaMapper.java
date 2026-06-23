package com.ssafy.home.qna.mapper;

import com.ssafy.home.qna.dto.QnaStatus;
import com.ssafy.home.qna.mapper.dto.QnaCreateParam;
import com.ssafy.home.qna.mapper.dto.QnaResult;
import com.ssafy.home.qna.mapper.dto.QnaUpdateParam;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QnaMapper {

    long countAll(@Param("status") QnaStatus status);

    List<QnaResult> findAll(
            @Param("offset") int offset,
            @Param("size") int size,
            @Param("status") QnaStatus status
    );

    QnaResult findById(@Param("qnaId") Long qnaId);

    void insert(QnaCreateParam qna);

    int updateById(QnaUpdateParam qna);

    int deleteById(@Param("qnaId") Long qnaId);
}
