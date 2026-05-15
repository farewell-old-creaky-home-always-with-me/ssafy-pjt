package com.ssafy.home.commercial.mapper;

import com.ssafy.home.commercial.dto.CommercialEntity;
import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommercialMapper {

    List<CommercialEntity> findCommercials(
            @Param("lat") BigDecimal lat,
            @Param("lng") BigDecimal lng,
            @Param("radius") int radius,
            @Param("category") String category
    );
}
