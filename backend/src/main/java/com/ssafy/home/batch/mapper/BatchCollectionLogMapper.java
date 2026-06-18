package com.ssafy.home.batch.mapper;

import com.ssafy.home.batch.domain.BatchCollectionLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BatchCollectionLogMapper {
    int insert(BatchCollectionLog log);
}
