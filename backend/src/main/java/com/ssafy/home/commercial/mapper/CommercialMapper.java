package com.ssafy.home.commercial.mapper;

import com.ssafy.home.commercial.mapper.dto.CommercialResult;
import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommercialMapper {

    List<CommercialResult> findAllByLocation(
            @Param("lat") BigDecimal lat,
            @Param("lng") BigDecimal lng,
            @Param("radius") int radius,
            @Param("category") String category
    );
}
