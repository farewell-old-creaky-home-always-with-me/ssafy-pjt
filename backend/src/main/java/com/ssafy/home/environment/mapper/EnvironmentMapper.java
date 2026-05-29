package com.ssafy.home.environment.mapper;

import com.ssafy.home.environment.dto.EnvironmentEntity;
import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EnvironmentMapper {

    List<EnvironmentEntity> findEnvironmentInfos(
            @Param("lat") BigDecimal lat,
            @Param("lng") BigDecimal lng,
            @Param("radius") int radius
    );
}
