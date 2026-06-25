package com.ssafy.home.demographics.mapper;

import com.ssafy.home.demographics.mapper.dto.DemographicsResult;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DemographicsMapper {

    Optional<DemographicsResult> findLatestByLocation(
            @Param("sidoName") String sidoName,
            @Param("sigunguName") String sigunguName,
            @Param("dongName") String dongName
    );
}
