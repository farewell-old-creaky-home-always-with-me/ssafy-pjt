package com.ssafy.home.house.mapper;

import com.ssafy.home.house.dto.HouseDealRow;
import com.ssafy.home.house.dto.HouseDetailRow;
import com.ssafy.home.house.dto.HouseSearchCondition;
import com.ssafy.home.house.dto.HouseSummaryRow;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HouseMapper {

    boolean existsRegionCode(@Param("regionCode") String regionCode);

    long countHouses(HouseSearchCondition condition);

    List<HouseSummaryRow> findHouseSummaries(HouseSearchCondition condition);

    HouseDetailRow findHouseById(@Param("houseId") Long houseId);

    List<HouseDealRow> findHouseDealsByHouseId(@Param("houseId") Long houseId);
}
