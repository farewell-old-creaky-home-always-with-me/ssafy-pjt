package com.ssafy.home.batch.report;

import com.ssafy.home.batch.report.dto.BatchCollectionLogResult;
import com.ssafy.home.batch.report.dto.BatchReportAiUpdateParam;
import com.ssafy.home.batch.report.dto.BatchReportCreateParam;
import com.ssafy.home.batch.report.dto.BatchReportFailureUpdateParam;
import com.ssafy.home.batch.report.dto.BatchReportPdfUpdateParam;
import com.ssafy.home.batch.report.dto.BatchReportResult;
import com.ssafy.home.batch.report.dto.HouseDealReportRow;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BatchReportMapper {

    void insert(BatchReportCreateParam report);

    int updateAiResult(BatchReportAiUpdateParam report);

    int updatePdfResult(BatchReportPdfUpdateParam report);

    int updateFailure(BatchReportFailureUpdateParam report);

    BatchReportResult findById(@Param("reportId") Long reportId);

    BatchCollectionLogResult findLatestHouseDealCollectionLog();

    List<HouseDealReportRow> findRecentHouseDeals(
            @Param("regionCode") String regionCode,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("limit") int limit
    );
}
