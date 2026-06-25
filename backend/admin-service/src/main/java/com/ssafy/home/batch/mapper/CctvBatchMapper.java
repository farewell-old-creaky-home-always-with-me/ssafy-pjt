package com.ssafy.home.batch.mapper;

import com.ssafy.home.batch.domain.NormalizedCctv;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CctvBatchMapper {

    int upsert(NormalizedCctv cctv);
}
