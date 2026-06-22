package com.ssafy.home.notice.mapper;

import com.ssafy.home.notice.mapper.dto.NoticeCreateParam;
import com.ssafy.home.notice.mapper.dto.NoticeResult;
import com.ssafy.home.notice.mapper.dto.NoticeUpdateParam;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NoticeMapper {

    long countAll();

    List<NoticeResult> findAll(@Param("offset") int offset, @Param("size") int size);

    NoticeResult findById(@Param("noticeId") Long noticeId);

    void insert(NoticeCreateParam notice);

    int updateById(NoticeUpdateParam notice);

    int deleteById(@Param("noticeId") Long noticeId);
}
