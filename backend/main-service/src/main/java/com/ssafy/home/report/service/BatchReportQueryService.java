package com.ssafy.home.report.service;

import static com.ssafy.home.global.exception.ErrorCode.BATCH_REPORT_NOT_FOUND;
import static com.ssafy.home.global.exception.ErrorCode.BATCH_REPORT_PDF_NOT_FOUND;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.report.BatchReportProperties;
import com.ssafy.home.report.dto.BatchReportResponse;
import com.ssafy.home.report.mapper.BatchReportMapper;
import com.ssafy.home.report.mapper.dto.BatchReportResult;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class BatchReportQueryService {

    private final BatchReportMapper batchReportMapper;
    private final BatchReportProperties properties;

    public BatchReportQueryService(BatchReportMapper batchReportMapper, BatchReportProperties properties) {
        this.batchReportMapper = batchReportMapper;
        this.properties = properties;
    }

    public BatchReportResponse getLatestReport() {
        BatchReportResult report = batchReportMapper.findLatestCompleted();
        if (report == null) {
            throw new CustomException(BATCH_REPORT_NOT_FOUND);
        }
        return toResponse(report);
    }

    public BatchReportResponse getReport(Long reportId) {
        return toResponse(findReport(reportId));
    }

    public Path getReportPdf(Long reportId) {
        BatchReportResult report = findReport(reportId);
        if (!StringUtils.hasText(report.getPdfFilePath())) {
            throw new CustomException(BATCH_REPORT_PDF_NOT_FOUND);
        }
        Path baseDir = properties.outputDir().toAbsolutePath().normalize();
        Path filePath = Path.of(report.getPdfFilePath()).toAbsolutePath().normalize();
        if (!filePath.startsWith(baseDir) || !Files.exists(filePath)) {
            throw new CustomException(BATCH_REPORT_PDF_NOT_FOUND);
        }
        return filePath;
    }

    public String getReportPdfFileName(Long reportId) {
        return findReport(reportId).getPdfFileName();
    }

    private BatchReportResult findReport(Long reportId) {
        BatchReportResult report = batchReportMapper.findCompletedById(reportId);
        if (report == null) {
            throw new CustomException(BATCH_REPORT_NOT_FOUND);
        }
        return report;
    }

    private BatchReportResponse toResponse(BatchReportResult report) {
        return new BatchReportResponse(
                report.getId(),
                report.getReportType(),
                report.getSourceType(),
                report.getRegionCode(),
                report.getYearMonth(),
                report.getSummary(),
                report.getTranslatedSummary(),
                report.getStatus(),
                report.getPdfFileName(),
                report.getCreatedAt(),
                report.getUpdatedAt()
        );
    }
}
