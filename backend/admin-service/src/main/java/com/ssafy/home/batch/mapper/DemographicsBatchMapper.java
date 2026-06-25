package com.ssafy.home.batch.mapper;

import com.ssafy.home.batch.domain.NormalizedForeignResident;
import com.ssafy.home.batch.domain.NormalizedPopulation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DemographicsBatchMapper {
    int upsertPopulation(NormalizedPopulation population);
    int upsertForeignResident(NormalizedForeignResident foreignResident);
}
