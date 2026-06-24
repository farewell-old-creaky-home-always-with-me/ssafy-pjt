package com.ssafy.home.batch.report.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchCollectionLogResult {
    private Long id;
    private Long jobExecutionId;
    private String jobName;
    private String dataType;
    private String regionCode;
    private String yearMonth;
    private String houseType;
    private String dealType;
    private Long collectedCount;
    private Long skippedCount;
    private Integer failedCount;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
