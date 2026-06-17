package com.ssafy.home.notice.mapper;

import com.ssafy.home.notice.mapper.dto.NoticeParam;
import com.ssafy.home.notice.mapper.dto.NoticeResult;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NoticeMapper {

    long count();

    List<NoticeResult> findAll(@Param("offset") int offset, @Param("size") int size);

    NoticeResult findById(@Param("noticeId") Long noticeId);

    void insert(NoticeParam notice);

    int update(NoticeParam notice);

    int delete(@Param("noticeId") Long noticeId);
}
