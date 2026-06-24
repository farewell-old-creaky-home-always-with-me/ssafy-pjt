package com.ssafy.home.batch.report.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchReportCreateParam {
    private Long id;
    private Long jobExecutionId;
    private Long batchCollectionLogId;
    private String reportType;
    private String sourceType;
    private String regionCode;
    private String yearMonth;
    private String status;
}
