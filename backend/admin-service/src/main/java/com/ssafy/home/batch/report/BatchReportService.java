package com.ssafy.home.batch.report;

import com.ssafy.home.batch.report.dto.BatchAiReportRequest;
import com.ssafy.home.batch.report.dto.BatchAiReportResult;
import com.ssafy.home.batch.report.dto.BatchCollectionLogResult;
import com.ssafy.home.batch.report.dto.BatchReportAiUpdateParam;
import com.ssafy.home.batch.report.dto.BatchReportCreateParam;
import com.ssafy.home.batch.report.dto.BatchReportFailureUpdateParam;
import com.ssafy.home.batch.report.dto.BatchReportPdfUpdateParam;
import com.ssafy.home.batch.report.dto.HouseDealReportRow;
import com.ssafy.home.batch.report.dto.PdfReportRequest;
import com.ssafy.home.batch.report.dto.PdfReportResult;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BatchReportService {

    private static final DateTimeFormatter YEAR_MONTH = DateTimeFormatter.ofPattern("yyyyMM");
    private static final int ERROR_MESSAGE_LIMIT = 1000;

    private final BatchReportMapper batchReportMapper;
    private final BatchAiClient batchAiClient;
    private final PdfReportService pdfReportService;
    private final BatchReportProperties properties;

    public BatchReportService(
            BatchReportMapper batchReportMapper,
            BatchAiClient batchAiClient,
            PdfReportService pdfReportService,
            BatchReportProperties properties
    ) {
        this.batchReportMapper = batchReportMapper;
        this.batchAiClient = batchAiClient;
        this.pdfReportService = pdfReportService;
        this.properties = properties;
    }

    public Long generateLatestHouseDealReport(Long jobExecutionId) {
        BatchCollectionLogResult log = batchReportMapper.findLatestHouseDealCollectionLog();
        if (log == null) {
            throw new IllegalStateException("No house deal collection log found");
        }

        BatchReportCreateParam createParam = createReport(jobExecutionId, log);
        batchReportMapper.insert(createParam);

        try {
            List<HouseDealReportRow> deals = findReportDeals(log);
            BatchAiReportResult aiResult = batchAiClient.createReport(new BatchAiReportRequest(
                    log.getRegionCode(),
                    log.getYearMonth(),
                    log.getCollectedCount(),
                    log.getSkippedCount(),
                    log.getFailedCount(),
                    deals
            ));
            updateAiResult(createParam.getId(), aiResult);

            PdfReportResult pdfResult = pdfReportService.generate(new PdfReportRequest(
                    createParam.getId(),
                    BatchReportType.REFLECTION.name(),
                    log.getRegionCode(),
                    log.getYearMonth(),
                    log.getCollectedCount(),
                    log.getSkippedCount(),
                    log.getFailedCount(),
                    aiResult.summary(),
                    aiResult.translatedSummary(),
                    deals
            ));
            updatePdfResult(createParam.getId(), pdfResult);
            return createParam.getId();
        } catch (RuntimeException exception) {
            updateFailure(createParam.getId(), exception);
            throw exception;
        }
    }

    private BatchReportCreateParam createReport(Long jobExecutionId, BatchCollectionLogResult log) {
        BatchReportCreateParam createParam = new BatchReportCreateParam();
        createParam.setJobExecutionId(jobExecutionId);
        createParam.setBatchCollectionLogId(log.getId());
        createParam.setReportType(BatchReportType.REFLECTION.name());
        createParam.setSourceType(BatchReportSourceType.HOUSE_DEAL.name());
        createParam.setRegionCode(log.getRegionCode());
        createParam.setYearMonth(log.getYearMonth());
        createParam.setStatus(BatchReportStatus.CREATED.name());
        return createParam;
    }

    private List<HouseDealReportRow> findReportDeals(BatchCollectionLogResult log) {
        DateRange range = toDateRange(log.getYearMonth());
        return batchReportMapper.findRecentHouseDeals(
                log.getRegionCode(),
                range.startDate(),
                range.endDate(),
                properties.maxDeals()
        );
    }

    private DateRange toDateRange(String yearMonth) {
        if (yearMonth == null || yearMonth.isBlank()) {
            return new DateRange(null, null);
        }
        try {
            YearMonth parsed = YearMonth.parse(yearMonth, YEAR_MONTH);
            return new DateRange(parsed.atDay(1), parsed.plusMonths(1).atDay(1));
        } catch (DateTimeParseException exception) {
            return new DateRange(null, null);
        }
    }

    private void updateAiResult(Long reportId, BatchAiReportResult result) {
        BatchReportAiUpdateParam param = new BatchReportAiUpdateParam();
        param.setId(reportId);
        param.setSummary(result.summary());
        param.setTranslatedSummary(result.translatedSummary());
        param.setStatus(BatchReportStatus.AI_COMPLETED.name());
        batchReportMapper.updateAiResult(param);
    }

    private void updatePdfResult(Long reportId, PdfReportResult result) {
        BatchReportPdfUpdateParam param = new BatchReportPdfUpdateParam();
        param.setId(reportId);
        param.setPdfFileName(result.fileName());
        param.setPdfFilePath(result.filePath());
        param.setStatus(BatchReportStatus.PDF_COMPLETED.name());
        batchReportMapper.updatePdfResult(param);
    }

    private void updateFailure(Long reportId, RuntimeException exception) {
        BatchReportFailureUpdateParam param = new BatchReportFailureUpdateParam();
        param.setId(reportId);
        param.setErrorMessage(limitErrorMessage(exception.getMessage()));
        param.setStatus(BatchReportStatus.FAILED.name());
        batchReportMapper.updateFailure(param);
    }

    private String limitErrorMessage(String message) {
        if (message == null) {
            return null;
        }
        return message.length() <= ERROR_MESSAGE_LIMIT
                ? message
                : message.substring(0, ERROR_MESSAGE_LIMIT);
    }

    private record DateRange(LocalDate startDate, LocalDate endDate) {
    }
}
