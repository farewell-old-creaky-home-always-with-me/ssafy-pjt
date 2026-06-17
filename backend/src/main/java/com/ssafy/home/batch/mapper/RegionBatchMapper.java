package com.ssafy.home.batch.mapper;

import com.ssafy.home.batch.domain.NormalizedRegionCode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RegionBatchMapper {

    int upsertRegionCode(NormalizedRegionCode regionCode);
}
