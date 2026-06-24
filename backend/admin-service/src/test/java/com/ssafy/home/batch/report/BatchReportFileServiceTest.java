package com.ssafy.home.batch.report;

import static com.ssafy.home.global.exception.ErrorCode.BATCH_REPORT_PDF_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.BDDMockito.given;

import com.ssafy.home.batch.report.dto.BatchReportPdfFile;
import com.ssafy.home.batch.report.dto.BatchReportResult;
import com.ssafy.home.global.exception.CustomException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BatchReportFileServiceTest {

    @TempDir
    private Path tempDir;

    @Mock
    private BatchReportMapper batchReportMapper;

    private Path outputDir;
    private BatchReportFileService batchReportFileService;

    @BeforeEach
    void setUp() throws IOException {
        outputDir = Files.createDirectory(tempDir.resolve("reports"));
        batchReportFileService = new BatchReportFileService(
                batchReportMapper,
                new BatchReportProperties(outputDir, "", 20)
        );
    }

    @Test
    @DisplayName("PDF path inside output directory is returned")
    void getPdfFile() throws IOException {
        Path pdfPath = Files.writeString(outputDir.resolve("report.pdf"), "pdf");
        given(batchReportMapper.findById(1L)).willReturn(report(pdfPath));

        BatchReportPdfFile pdfFile = batchReportFileService.getPdfFile(1L);

        assertThat(pdfFile.path()).isEqualTo(pdfPath.toRealPath());
    }

    @Test
    @DisplayName("Symbolic link escaping output directory is rejected")
    void rejectSymlinkOutsideOutputDir() throws IOException {
        Path outsidePdf = Files.writeString(tempDir.resolve("outside.pdf"), "pdf");
        Path linkPath = outputDir.resolve("link.pdf");
        assumeTrue(createSymbolicLink(linkPath, outsidePdf));
        given(batchReportMapper.findById(1L)).willReturn(report(linkPath));

        assertThatThrownBy(() -> batchReportFileService.getPdfFile(1L))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(BATCH_REPORT_PDF_NOT_FOUND));
    }

    private boolean createSymbolicLink(Path linkPath, Path targetPath) {
        try {
            Files.createSymbolicLink(linkPath, targetPath);
            return true;
        } catch (UnsupportedOperationException | IOException | SecurityException exception) {
            return false;
        }
    }

    private BatchReportResult report(Path pdfPath) {
        BatchReportResult report = new BatchReportResult();
        report.setId(1L);
        report.setPdfFileName("report.pdf");
        report.setPdfFilePath(pdfPath.toString());
        return report;
    }
}
