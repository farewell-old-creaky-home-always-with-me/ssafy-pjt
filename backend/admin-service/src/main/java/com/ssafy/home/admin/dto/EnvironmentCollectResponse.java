package com.ssafy.home.admin.dto;

public record EnvironmentCollectResponse(
        Long executionId,
        String jobName,
        String status
) {
}
