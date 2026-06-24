package com.ssafy.home.batchreport.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BatchReportSummaryRequest(
        String regionCode,
        String yearMonth,
        Long collectedCount,
        Long skippedCount,
        Integer failedCount,
        @NotNull List<HouseDealSummaryItem> deals
) {
}
