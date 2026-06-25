package com.ssafy.home.housinginfo.mapper;

import com.ssafy.home.housinginfo.mapper.dto.HousingInfoResult;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HousingInfoMapper {

    List<HousingInfoResult> findRecent(@Param("infoType") String infoType, @Param("limit") int limit);
}
