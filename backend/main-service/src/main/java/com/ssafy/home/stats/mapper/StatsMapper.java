package com.ssafy.home.stats.mapper;

import com.ssafy.home.stats.mapper.dto.StatsResult;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StatsMapper {
    StatsResult findSummary();
}
