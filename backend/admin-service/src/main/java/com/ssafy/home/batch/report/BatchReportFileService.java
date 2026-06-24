package com.ssafy.home.batch.report;

import static com.ssafy.home.global.exception.ErrorCode.BATCH_REPORT_NOT_FOUND;
import static com.ssafy.home.global.exception.ErrorCode.BATCH_REPORT_PDF_NOT_FOUND;

import com.ssafy.home.batch.report.dto.BatchReportPdfFile;
import com.ssafy.home.batch.report.dto.BatchReportResult;
import com.ssafy.home.global.exception.CustomException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class BatchReportFileService {

    private final BatchReportMapper batchReportMapper;
    private final BatchReportProperties properties;

    public BatchReportFileService(BatchReportMapper batchReportMapper, BatchReportProperties properties) {
        this.batchReportMapper = batchReportMapper;
        this.properties = properties;
    }

    public BatchReportPdfFile getPdfFile(Long reportId) {
        BatchReportResult report = batchReportMapper.findById(reportId);
        if (report == null) {
            throw new CustomException(BATCH_REPORT_NOT_FOUND);
        }
        if (!StringUtils.hasText(report.getPdfFilePath())) {
            throw new CustomException(BATCH_REPORT_PDF_NOT_FOUND);
        }
        Path baseDir = properties.outputDir().toAbsolutePath().normalize();
        Path filePath = Path.of(report.getPdfFilePath()).toAbsolutePath().normalize();
        if (!filePath.startsWith(baseDir) || !Files.exists(filePath)) {
            throw new CustomException(BATCH_REPORT_PDF_NOT_FOUND);
        }
        return new BatchReportPdfFile(report.getPdfFileName(), filePath);
    }
}
