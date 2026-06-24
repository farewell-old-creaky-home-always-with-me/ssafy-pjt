package com.ssafy.home.batch.report.dto;

import java.util.List;

public record BatchAiReportRequest(
        String regionCode,
        String yearMonth,
        Long collectedCount,
        Long skippedCount,
        Integer failedCount,
        List<HouseDealReportRow> deals
) {
}
