package com.ssafy.home.batch.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.ssafy.home.batch.report.dto.PdfReportRequest;
import java.io.IOException;
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
        Path fontPath = findUnicodeFontPath();
        assumeTrue(fontPath != null);
        PdfReportService pdfReportService = new PdfReportService(
                new BatchReportProperties(tempDir, fontPath.toString(), 5)
        );

        var result = pdfReportService.generate(request("11680", "202606"));

        assertThat(result.fileName()).endsWith(".pdf");
        assertThat(result.filePath()).startsWith(tempDir.toAbsolutePath().toString());
        assertThat(Files.exists(Path.of(result.filePath()))).isTrue();
    }

    @Test
    @DisplayName("PDF file name sanitizes request values")
    void generateWithUnsafeFileNameInputs() {
        Path fontPath = findUnicodeFontPath();
        assumeTrue(fontPath != null);
        PdfReportService pdfReportService = new PdfReportService(
                new BatchReportProperties(tempDir, fontPath.toString(), 5)
        );

        var result = pdfReportService.generate(request("../outside", "2026/06"));

        Path filePath = Path.of(result.filePath());
        assertThat(result.fileName()).doesNotContain("/", "\\");
        assertThat(filePath.normalize()).startsWith(tempDir.toAbsolutePath().normalize());
        assertThat(Files.exists(filePath)).isTrue();
    }

    @Test
    @DisplayName("PDF generation fails when font is not configured")
    void missingFont() {
        PdfReportService pdfReportService = new PdfReportService(
                new BatchReportProperties(tempDir, "", 5)
        );

        assertThatThrownBy(() -> pdfReportService.generate(request("11680", "202606")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("font must be configured");
    }

    private PdfReportRequest request(String regionCode, String yearMonth) {
        return new PdfReportRequest(
                1L,
                BatchReportType.REFLECTION.name(),
                regionCode,
                yearMonth,
                10L,
                1L,
                0,
                "한글 요약",
                "fake translated summary",
                List.of()
        );
    }

    private Path findUnicodeFontPath() {
        List<Path> candidates = List.of(
                Path.of("C:/Windows/Fonts/malgun.ttf"),
                Path.of("/usr/share/fonts/truetype/noto/NotoSansCJK-Regular.ttc"),
                Path.of("/usr/share/fonts/opentype/noto/NotoSansCJK-Regular.ttc"),
                Path.of("/System/Library/Fonts/AppleSDGothicNeo.ttc")
        );
        return candidates.stream()
                .filter(Files::isRegularFile)
                .filter(PdfReportServiceTest::isTrueTypeFont)
                .findFirst()
                .orElse(null);
    }

    // PDFBox only supports plain TTF (not TTC or OTF). Check magic bytes to filter candidates.
    private static boolean isTrueTypeFont(Path path) {
        try (var in = Files.newInputStream(path)) {
            byte[] magic = in.readNBytes(4);
            if (magic.length < 4) return false;
            int sig = ((magic[0] & 0xFF) << 24) | ((magic[1] & 0xFF) << 16)
                    | ((magic[2] & 0xFF) << 8) | (magic[3] & 0xFF);
            return sig == 0x00010000 || sig == 0x74727565; // TTF signatures
        } catch (IOException e) {
            return false;
        }
    }
}
