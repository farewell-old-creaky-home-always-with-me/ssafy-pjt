package com.ssafy.home.report.mapper;

import com.ssafy.home.report.mapper.dto.BatchReportResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BatchReportMapper {

    BatchReportResult findLatestCompleted();

    BatchReportResult findCompletedById(@Param("reportId") Long reportId);
}
