package com.ssafy.home.environment.mapper;

import com.ssafy.home.environment.mapper.dto.EnvironmentResult;
import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EnvironmentMapper {

    List<EnvironmentResult> findByLocation(
            @Param("lat") BigDecimal lat,
            @Param("lng") BigDecimal lng,
            @Param("radius") int radius
    );
}
