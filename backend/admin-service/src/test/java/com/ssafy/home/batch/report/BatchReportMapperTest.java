package com.ssafy.home.batch.report;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.batch.report.dto.BatchReportAiUpdateParam;
import com.ssafy.home.batch.report.dto.BatchReportCreateParam;
import com.ssafy.home.batch.report.dto.BatchReportPdfUpdateParam;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@MybatisTest
@Sql("/sql/batch-report-data.sql")
class BatchReportMapperTest {

    @Autowired
    private BatchReportMapper batchReportMapper;

    @Test
    @DisplayName("Batch report stores AI and PDF results")
    void insertAndUpdateReport() {
        BatchReportCreateParam createParam = new BatchReportCreateParam();
        createParam.setJobExecutionId(200L);
        createParam.setBatchCollectionLogId(1L);
        createParam.setReportType(BatchReportType.REFLECTION.name());
        createParam.setSourceType(BatchReportSourceType.HOUSE_DEAL.name());
        createParam.setRegionCode("11680");
        createParam.setYearMonth("202606");
        createParam.setStatus(BatchReportStatus.CREATED.name());

        batchReportMapper.insert(createParam);

        assertThat(createParam.getId()).isNotNull();
        assertThat(batchReportMapper.findById(createParam.getId()).getStatus())
                .isEqualTo(BatchReportStatus.CREATED.name());

        BatchReportAiUpdateParam aiParam = new BatchReportAiUpdateParam();
        aiParam.setId(createParam.getId());
        aiParam.setSummary("summary");
        aiParam.setTranslatedSummary("translated");
        aiParam.setStatus(BatchReportStatus.AI_COMPLETED.name());
        assertThat(batchReportMapper.updateAiResult(aiParam)).isEqualTo(1);

        BatchReportPdfUpdateParam pdfParam = new BatchReportPdfUpdateParam();
        pdfParam.setId(createParam.getId());
        pdfParam.setPdfFileName("report.pdf");
        pdfParam.setPdfFilePath("/tmp/report.pdf");
        pdfParam.setStatus(BatchReportStatus.PDF_COMPLETED.name());
        assertThat(batchReportMapper.updatePdfResult(pdfParam)).isEqualTo(1);

        var report = batchReportMapper.findById(createParam.getId());
        assertThat(report.getSummary()).isEqualTo("summary");
        assertThat(report.getPdfFileName()).isEqualTo("report.pdf");
    }

    @Test
    @DisplayName("Latest house deal collection log and sampled deals are selected")
    void findLatestLogAndRecentDeals() {
        var log = batchReportMapper.findLatestHouseDealCollectionLog();
        var deals = batchReportMapper.findRecentHouseDeals(
                "11680",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 7, 1),
                10
        );

        assertThat(log.getId()).isEqualTo(1L);
        assertThat(log.getCollectedCount()).isEqualTo(10L);
        assertThat(deals).hasSize(1);
        assertThat(deals.get(0).getAptName()).isEqualTo("Yeoksam Raemian");
    }
}
