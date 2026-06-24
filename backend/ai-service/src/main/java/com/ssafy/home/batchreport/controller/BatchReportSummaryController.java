package com.ssafy.home.batchreport.controller;

import com.ssafy.home.batchreport.dto.BatchReportSummaryRequest;
import com.ssafy.home.batchreport.dto.BatchReportSummaryResponse;
import com.ssafy.home.batchreport.service.BatchReportSummaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/batch/reports")
@RequiredArgsConstructor
public class BatchReportSummaryController {

    private final BatchReportSummaryService batchReportSummaryService;

    @PostMapping("/summary")
    public ResponseEntity<BatchReportSummaryResponse> summarize(
            @Valid @RequestBody BatchReportSummaryRequest request
    ) {
        return ResponseEntity.ok(batchReportSummaryService.summarize(request));
    }
}
