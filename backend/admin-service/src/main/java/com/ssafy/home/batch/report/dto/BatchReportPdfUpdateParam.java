package com.ssafy.home.batch.report.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchReportPdfUpdateParam {
    private Long id;
    private String pdfFileName;
    private String pdfFilePath;
    private String status;
}
