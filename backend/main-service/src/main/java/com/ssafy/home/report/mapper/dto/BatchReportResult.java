package com.ssafy.home.report.mapper.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchReportResult {
    private Long id;
    private String reportType;
    private String sourceType;
    private String regionCode;
    private String yearMonth;
    private String summary;
    private String translatedSummary;
    private String pdfFileName;
    private String pdfFilePath;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
