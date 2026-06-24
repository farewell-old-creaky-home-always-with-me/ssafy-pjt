package com.ssafy.home.batch.mapper;

import com.ssafy.home.batch.domain.NormalizedHouseDeal;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HouseDealBatchMapper {
    boolean existsRegionCode(@Param("regionCode") String regionCode);
    List<String> findAllLawdCodes();
    Long findHouseId(NormalizedHouseDeal deal);
    int insertHouse(NormalizedHouseDeal deal);
    int insertDealIfAbsent(@Param("houseId") long houseId,
                           @Param("deal") NormalizedHouseDeal deal);
    boolean existsDeal(@Param("houseId") long houseId,
                       @Param("deal") NormalizedHouseDeal deal);
}
