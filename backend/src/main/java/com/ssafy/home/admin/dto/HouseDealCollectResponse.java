package com.ssafy.home.admin.dto;

public record HouseDealCollectResponse(
        Long jobExecutionId,
        String jobName,
        String status,
        Parameters parameters
) {
    public record Parameters(
            String regionCode,
            String yearMonth,
            String houseType,
            String dealType
    ) {
    }
}
