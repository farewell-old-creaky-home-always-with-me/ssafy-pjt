package com.ssafy.home.region.mapper;

import com.ssafy.home.region.mapper.dto.RegionResult;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RegionMapper {
    List<RegionResult> findAll(@Param("dong") String dong);
}
