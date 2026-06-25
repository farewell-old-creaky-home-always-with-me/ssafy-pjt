package com.ssafy.home.admin.dto;

public record CctvCollectResponse(
        Long jobExecutionId,
        String jobName,
        String status
) {
}
