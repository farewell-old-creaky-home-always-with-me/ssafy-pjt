package com.ssafy.home.news.mapper;

import com.ssafy.home.news.mapper.dto.NewsResult;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NewsMapper {

    List<NewsResult> findRecent(@Param("limit") int limit);
}
