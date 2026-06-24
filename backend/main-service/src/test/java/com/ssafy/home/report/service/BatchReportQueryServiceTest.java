package com.ssafy.home.report.service;

import static com.ssafy.home.global.exception.ErrorCode.BATCH_REPORT_NOT_FOUND;
import static com.ssafy.home.global.exception.ErrorCode.BATCH_REPORT_PDF_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.report.BatchReportProperties;
import com.ssafy.home.report.mapper.BatchReportMapper;
import com.ssafy.home.report.mapper.dto.BatchReportResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BatchReportQueryServiceTest {

    @TempDir
    private Path tempDir;

    @Mock
    private BatchReportMapper batchReportMapper;

    private Path outputDir;
    private BatchReportQueryService batchReportQueryService;

    @BeforeEach
    void setUp() throws IOException {
        outputDir = Files.createDirectory(tempDir.resolve("reports"));
        batchReportQueryService = new BatchReportQueryService(
                batchReportMapper,
                new BatchReportProperties(outputDir)
        );
    }

    @Test
    @DisplayName("최신 배치 보고서를 조회한다")
    void getLatestReport() {
        given(batchReportMapper.findLatestCompleted()).willReturn(report(1L, null));

        var response = batchReportQueryService.getLatestReport();

        assertThat(response.reportId()).isEqualTo(1L);
        assertThat(response.summary()).isEqualTo("요약");
    }

    @Test
    @DisplayName("ID로 배치 보고서를 조회한다")
    void getReport() {
        given(batchReportMapper.findCompletedById(1L)).willReturn(report(1L, null));

        var response = batchReportQueryService.getReport(1L);

        assertThat(response.reportId()).isEqualTo(1L);
        assertThat(response.translatedSummary()).isEqualTo("Summary");
    }

    @Test
    @DisplayName("보고서 PDF 경로를 조회한다")
    void getReportPdf() throws IOException {
        Path pdfPath = Files.writeString(outputDir.resolve("report.pdf"), "pdf");
        given(batchReportMapper.findCompletedById(1L)).willReturn(report(1L, pdfPath));

        Path found = batchReportQueryService.getReportPdf(1L);

        assertThat(found).isEqualTo(pdfPath.toAbsolutePath().normalize());
    }

    @Test
    @DisplayName("보고서가 없으면 예외가 발생한다")
    void getReportNotFound() {
        given(batchReportMapper.findCompletedById(99L)).willReturn(null);

        assertThatThrownBy(() -> batchReportQueryService.getReport(99L))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(BATCH_REPORT_NOT_FOUND));
    }

    @Test
    @DisplayName("PDF 파일이 없으면 예외가 발생한다")
    void getReportPdfMissingFile() {
        Path missingPath = outputDir.resolve("missing.pdf");
        given(batchReportMapper.findCompletedById(1L)).willReturn(report(1L, missingPath));

        assertThatThrownBy(() -> batchReportQueryService.getReportPdf(1L))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(BATCH_REPORT_PDF_NOT_FOUND));
    }

    @Test
    @DisplayName("PDF 경로가 출력 디렉터리 밖이면 예외가 발생한다")
    void getReportPdfRejectsPathTraversal() throws IOException {
        Path outsidePath = Files.writeString(tempDir.resolve("outside.pdf"), "pdf");
        given(batchReportMapper.findCompletedById(1L)).willReturn(report(1L, outsidePath));

        assertThatThrownBy(() -> batchReportQueryService.getReportPdf(1L))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(BATCH_REPORT_PDF_NOT_FOUND));
    }

    private BatchReportResult report(Long id, Path pdfPath) {
        BatchReportResult result = new BatchReportResult();
        result.setId(id);
        result.setReportType("REFLECTION");
        result.setSourceType("HOUSE_DEAL");
        result.setRegionCode("11680");
        result.setYearMonth("202606");
        result.setSummary("요약");
        result.setTranslatedSummary("Summary");
        result.setStatus("PDF_COMPLETED");
        result.setPdfFileName("보고서.pdf");
        result.setPdfFilePath(pdfPath == null ? null : pdfPath.toString());
        result.setCreatedAt(LocalDateTime.of(2026, 6, 24, 10, 0));
        result.setUpdatedAt(LocalDateTime.of(2026, 6, 24, 10, 30));
        return result;
    }
}
