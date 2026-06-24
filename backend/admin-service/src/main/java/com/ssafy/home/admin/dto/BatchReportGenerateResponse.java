package com.ssafy.home.admin.dto;

public record BatchReportGenerateResponse(
        Long jobExecutionId,
        String jobName,
        String status
) {
}
