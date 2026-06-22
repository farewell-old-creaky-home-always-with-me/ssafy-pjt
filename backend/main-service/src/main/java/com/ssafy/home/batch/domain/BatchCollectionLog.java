package com.ssafy.home.batch.domain;

import java.time.LocalDateTime;

public record BatchCollectionLog(
        long jobExecutionId, String jobName, String dataType,
        String regionCode, String yearMonth, String houseType, String dealType,
        long collectedCount, long skippedCount, int failedCount, String status,
        LocalDateTime startedAt, LocalDateTime endedAt
) {
}
