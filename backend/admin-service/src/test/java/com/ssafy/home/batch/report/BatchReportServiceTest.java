package com.ssafy.home.batch.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ssafy.home.batch.report.dto.BatchCollectionLogResult;
import com.ssafy.home.batch.report.dto.BatchReportAiUpdateParam;
import com.ssafy.home.batch.report.dto.BatchReportCreateParam;
import com.ssafy.home.batch.report.dto.BatchReportPdfUpdateParam;
import com.ssafy.home.batch.report.dto.PdfReportResult;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BatchReportServiceTest {

    @Mock
    private BatchReportMapper batchReportMapper;

    @Mock
    private BatchAiClient batchAiClient;

    @Mock
    private PdfReportService pdfReportService;

    private BatchReportService batchReportService;

    @BeforeEach
    void setUp() {
        batchReportService = new BatchReportService(
                batchReportMapper,
                batchAiClient,
                pdfReportService,
                new BatchReportProperties(Path.of("build/test-reports"), "", 20)
        );
    }

    @Test
    @DisplayName("Latest house deal collection log is converted to AI and PDF report")
    void generateLatestHouseDealReport() {
        given(batchReportMapper.findLatestHouseDealCollectionLog()).willReturn(collectionLog());
        given(batchReportMapper.findRecentHouseDeals(
                "11680",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 7, 1),
                20
        )).willReturn(List.of());
        given(batchAiClient.createReport(any())).willReturn(new com.ssafy.home.batch.report.dto.BatchAiReportResult(
                "summary",
                "translated"
        ));
        given(pdfReportService.generate(any())).willReturn(new PdfReportResult("report.pdf", "/tmp/report.pdf"));
        org.mockito.BDDMockito.willAnswer(invocation -> {
            BatchReportCreateParam param = invocation.getArgument(0);
            param.setId(11L);
            return null;
        }).given(batchReportMapper).insert(any());

        Long reportId = batchReportService.generateLatestHouseDealReport(100L);

        assertThat(reportId).isEqualTo(11L);
        ArgumentCaptor<BatchReportAiUpdateParam> aiCaptor = ArgumentCaptor.forClass(BatchReportAiUpdateParam.class);
        then(batchReportMapper).should().updateAiResult(aiCaptor.capture());
        assertThat(aiCaptor.getValue().getStatus()).isEqualTo(BatchReportStatus.AI_COMPLETED.name());

        ArgumentCaptor<BatchReportPdfUpdateParam> pdfCaptor = ArgumentCaptor.forClass(BatchReportPdfUpdateParam.class);
        then(batchReportMapper).should().updatePdfResult(pdfCaptor.capture());
        assertThat(pdfCaptor.getValue().getStatus()).isEqualTo(BatchReportStatus.PDF_COMPLETED.name());
    }

    private BatchCollectionLogResult collectionLog() {
        BatchCollectionLogResult log = new BatchCollectionLogResult();
        log.setId(1L);
        log.setRegionCode("11680");
        log.setYearMonth("202606");
        log.setCollectedCount(10L);
        log.setSkippedCount(1L);
        log.setFailedCount(0);
        return log;
    }
}
