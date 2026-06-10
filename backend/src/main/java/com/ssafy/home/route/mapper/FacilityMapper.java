package com.ssafy.home.route.mapper;

import com.ssafy.home.route.dto.FacilityEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FacilityMapper {
    List<FacilityEntity> findAll();
}
