package com.ssafy.home.stats.mapper;

import com.ssafy.home.stats.dto.StatsRow;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StatsMapper {
    StatsRow findStats();
}
