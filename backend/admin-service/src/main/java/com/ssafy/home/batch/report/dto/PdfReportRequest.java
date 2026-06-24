package com.ssafy.home.batch.report.dto;

import java.util.List;

public record PdfReportRequest(
        Long reportId,
        String reportType,
        String regionCode,
        String yearMonth,
        Long collectedCount,
        Long skippedCount,
        Integer failedCount,
        String summary,
        String translatedSummary,
        List<HouseDealReportRow> deals
) {
}
