package com.ssafy.home.house.mapper;

import com.ssafy.home.house.mapper.dto.HouseDealResult;
import com.ssafy.home.house.mapper.dto.HouseDetailResult;
import com.ssafy.home.house.mapper.dto.HouseSearchParam;
import com.ssafy.home.house.mapper.dto.HouseSummaryResult;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HouseMapper {

    boolean existsByRegionCode(@Param("regionCode") String regionCode);

    long countBySearch(HouseSearchParam condition);

    List<HouseSummaryResult> search(HouseSearchParam condition);

    HouseDetailResult findById(@Param("houseId") Long houseId);

    List<HouseDealResult> findAllByHouseId(@Param("houseId") Long houseId);
}
