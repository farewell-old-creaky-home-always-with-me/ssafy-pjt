package com.ssafy.home.report.dto;

import java.time.LocalDateTime;

public record BatchReportResponse(
        Long reportId,
        String reportType,
        String sourceType,
        String regionCode,
        String yearMonth,
        String summary,
        String translatedSummary,
        String status,
        String pdfFileName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
