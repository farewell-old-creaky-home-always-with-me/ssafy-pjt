package com.ssafy.home.batch.mapper;

import com.ssafy.home.batch.domain.NormalizedCommercialArea;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommercialAreaBatchMapper {

    int upsert(NormalizedCommercialArea area);

    List<String> findAllSigunguCodes();
}
