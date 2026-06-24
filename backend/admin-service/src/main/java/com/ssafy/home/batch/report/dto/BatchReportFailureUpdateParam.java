package com.ssafy.home.batch.report.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchReportFailureUpdateParam {
    private Long id;
    private String errorMessage;
    private String status;
}
