package com.ssafy.home.batch.report;

import com.ssafy.home.batch.report.dto.BatchAiReportRequest;
import com.ssafy.home.batch.report.dto.BatchAiReportResult;

public interface BatchAiClient {

    BatchAiReportResult createReport(BatchAiReportRequest request);
}
