package com.ssafy.home.notice.mapper;

import com.ssafy.home.notice.dto.NoticeEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NoticeMapper {

    long countNotices();

    List<NoticeEntity> findNotices(@Param("offset") int offset, @Param("size") int size);

    NoticeEntity findNoticeById(@Param("noticeId") Long noticeId);

    void insertNotice(NoticeEntity notice);

    int updateNotice(NoticeEntity notice);

    int deleteNoticeById(@Param("noticeId") Long noticeId);
}
