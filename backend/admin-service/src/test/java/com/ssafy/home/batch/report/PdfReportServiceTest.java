package com.ssafy.home.batch.report;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.batch.report.dto.PdfReportRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PdfReportServiceTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("PDF report is generated under configured output directory")
    void generate() {
        PdfReportService pdfReportService = new PdfReportService(
                new BatchReportProperties(tempDir, "", 5)
        );

        var result = pdfReportService.generate(new PdfReportRequest(
                1L,
                BatchReportType.REFLECTION.name(),
                "11680",
                "202606",
                10L,
                1L,
                0,
                "fake summary",
                "fake translated summary",
                List.of()
        ));

        assertThat(result.fileName()).endsWith(".pdf");
        assertThat(result.filePath()).startsWith(tempDir.toAbsolutePath().toString());
        assertThat(Files.exists(Path.of(result.filePath()))).isTrue();
    }
}
