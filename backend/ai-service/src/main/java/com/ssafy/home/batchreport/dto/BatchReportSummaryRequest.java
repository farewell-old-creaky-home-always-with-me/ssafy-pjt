package com.ssafy.home.batchreport.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

public record BatchReportSummaryRequest(
        @NotBlank
        String regionCode,
        @NotBlank
        String yearMonth,
        @NotNull
        @PositiveOrZero
        Long collectedCount,
        @NotNull
        @PositiveOrZero
        Long skippedCount,
        @NotNull
        @PositiveOrZero
        Integer failedCount,
        @NotEmpty
        List<@Valid HouseDealSummaryItem> deals
) {
}
