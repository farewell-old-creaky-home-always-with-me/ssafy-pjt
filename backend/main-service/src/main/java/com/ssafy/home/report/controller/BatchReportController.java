package com.ssafy.home.report.controller;

import com.ssafy.home.report.dto.BatchReportResponse;
import com.ssafy.home.report.service.BatchReportQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports/batch")
@RequiredArgsConstructor
public class BatchReportController {

    private final BatchReportQueryService batchReportQueryService;

    @GetMapping("/latest")
    public ResponseEntity<BatchReportResponse> getLatestReport() {
        return ResponseEntity.ok(batchReportQueryService.getLatestReport());
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<BatchReportResponse> getReport(@PathVariable Long reportId) {
        return ResponseEntity.ok(batchReportQueryService.getReport(reportId));
    }

    @GetMapping("/{reportId}/pdf")
    public ResponseEntity<Resource> downloadReportPdf(@PathVariable Long reportId) {
        String fileName = batchReportQueryService.getReportPdfFileName(reportId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(new FileSystemResource(batchReportQueryService.getReportPdf(reportId)));
    }
}
