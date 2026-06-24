package com.ssafy.home.batch.report.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchReportAiUpdateParam {
    private Long id;
    private String summary;
    private String translatedSummary;
    private String status;
}
