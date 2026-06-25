package com.ssafy.home.batch.mapper;

import com.ssafy.home.batch.domain.NormalizedEnvironment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EnvironmentBatchMapper {

    int upsert(NormalizedEnvironment environment);
}
