package com.ssafy.home.batch.mapper;

import com.ssafy.home.batch.domain.NormalizedHousingInfo;
import com.ssafy.home.batch.domain.NormalizedHousingNews;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HousingNewsBatchMapper {

    int upsertNews(NormalizedHousingNews news);

    int upsertInfo(NormalizedHousingInfo info);
}
