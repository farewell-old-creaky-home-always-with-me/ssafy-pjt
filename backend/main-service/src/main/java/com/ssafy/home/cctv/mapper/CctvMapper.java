package com.ssafy.home.cctv.mapper;

import com.ssafy.home.cctv.mapper.dto.CctvResult;
import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CctvMapper {

    List<CctvResult> findAllByLocation(
            @Param("lat") BigDecimal lat,
            @Param("lng") BigDecimal lng,
            @Param("radius") int radius
    );
}
