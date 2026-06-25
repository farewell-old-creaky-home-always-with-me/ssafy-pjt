package com.ssafy.home.admin.controller;

import com.ssafy.home.admin.dto.BatchReportGenerateResponse;
import com.ssafy.home.admin.dto.CommercialAreaCollectResponse;
import com.ssafy.home.admin.dto.EnvironmentCollectResponse;
import com.ssafy.home.admin.dto.HouseDealCollectRequest;
import com.ssafy.home.admin.dto.HouseDealCollectResponse;
import com.ssafy.home.admin.dto.HousingNewsCollectResponse;
import com.ssafy.home.admin.dto.RegionCodeCollectResponse;
import com.ssafy.home.admin.service.BatchJobService;
import com.ssafy.home.batch.report.BatchReportFileService;
import com.ssafy.home.batch.report.dto.BatchReportPdfFile;
import com.ssafy.home.global.auth.LoginMemberId;
import com.ssafy.home.global.interceptor.AdminOnly;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AdminOnly
@RestController
@RequestMapping("/api/admin/batch")
@RequiredArgsConstructor
public class AdminBatchController {

    private final BatchJobService batchJobService;
    private final BatchReportFileService batchReportFileService;

    @PostMapping("/house-deals")
    public ResponseEntity<HouseDealCollectResponse> collectHouseDeals(
            @Valid @RequestBody HouseDealCollectRequest request,
            @LoginMemberId Long memberId
    ) {
        return ResponseEntity.ok(batchJobService.collectHouseDeals(memberId, request));
    }

    @PostMapping("/region-codes")
    public ResponseEntity<RegionCodeCollectResponse> collectRegionCodes(
            @LoginMemberId Long memberId
    ) {
        return ResponseEntity.ok(batchJobService.collectRegionCodes(memberId));
    }

    @PostMapping("/reports")
    public ResponseEntity<BatchReportGenerateResponse> generateBatchReport(
            @LoginMemberId Long memberId
    ) {
        return ResponseEntity.ok(batchJobService.generateBatchReport(memberId));
    }

    @PostMapping("/commercial")
    public ResponseEntity<CommercialAreaCollectResponse> collectCommercialAreas(
            @LoginMemberId Long memberId
    ) {
        return ResponseEntity.ok(batchJobService.collectCommercialAreas(memberId));
    }

    @PostMapping("/environment")
    public ResponseEntity<EnvironmentCollectResponse> collectEnvironment(
            @LoginMemberId Long memberId
    ) {
        return ResponseEntity.ok(batchJobService.collectEnvironment(memberId));
    }

    @PostMapping("/news")
    public ResponseEntity<HousingNewsCollectResponse> collectHousingNews(
            @LoginMemberId Long memberId
    ) {
        return ResponseEntity.ok(batchJobService.collectHousingNews(memberId));
    }

    @GetMapping("/reports/{reportId}/pdf")
    public ResponseEntity<Resource> downloadBatchReportPdf(
            @PathVariable Long reportId
    ) {
        BatchReportPdfFile pdfFile = batchReportFileService.getPdfFile(reportId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + pdfFile.fileName() + "\"")
                .body(new FileSystemResource(pdfFile.path()));
    }
}
