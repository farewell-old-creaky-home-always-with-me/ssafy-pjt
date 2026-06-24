package com.ssafy.home.batch.report;

import com.ssafy.home.batch.report.dto.HouseDealReportRow;
import com.ssafy.home.batch.report.dto.PdfReportRequest;
import com.ssafy.home.batch.report.dto.PdfReportResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PdfReportService {

    private static final DateTimeFormatter FILE_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final float MARGIN = 50F;
    private static final float LEADING = 16F;
    private static final int WRAP_LENGTH = 90;

    private final BatchReportProperties properties;

    public PdfReportService(BatchReportProperties properties) {
        this.properties = properties;
    }

    public PdfReportResult generate(PdfReportRequest request) {
        try {
            Path outputDir = properties.outputDir().toAbsolutePath().normalize();
            Files.createDirectories(outputDir);
            String fileName = buildFileName(request);
            Path filePath = outputDir.resolve(fileName).normalize();
            if (!filePath.startsWith(outputDir)) {
                throw new IllegalStateException("Batch report PDF path is outside output directory");
            }

            try (PDDocument document = new PDDocument()) {
                PDFont font = loadFont(document);
                writeLines(document, font, buildLines(request, true));
                document.save(filePath.toFile());
            }
            return new PdfReportResult(fileName, filePath.toString());
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to generate batch report PDF", exception);
        }
    }

    private String buildFileName(PdfReportRequest request) {
        return "batch-report-" + request.reportId()
                + "-" + sanitizeFileNamePart(valueOrDefault(request.regionCode(), "ALL"))
                + "-" + sanitizeFileNamePart(valueOrDefault(request.yearMonth(), "UNKNOWN"))
                + "-" + LocalDateTime.now().format(FILE_TIME)
                + ".pdf";
    }

    private String sanitizeFileNamePart(String value) {
        String sanitized = value.replaceAll("[^A-Za-z0-9._-]", "_");
        return sanitized.isBlank() ? "UNKNOWN" : sanitized;
    }

    private PDFont loadFont(PDDocument document) throws IOException {
        if (!hasExternalFont()) {
            throw new IllegalStateException("Batch report PDF font must be configured with a readable font file");
        }
        return PDType0Font.load(document, Path.of(properties.fontPath()).toFile());
    }

    private boolean hasExternalFont() {
        return StringUtils.hasText(properties.fontPath()) && Files.isRegularFile(Path.of(properties.fontPath()));
    }

    private List<String> buildLines(PdfReportRequest request, boolean externalFont) {
        List<String> lines = new ArrayList<>();
        lines.add("Batch AI Summary Report");
        lines.add("Report ID: " + request.reportId());
        lines.add("Report Type: " + valueOrDefault(request.reportType(), BatchReportType.REFLECTION.name()));
        lines.add("Region Code: " + valueOrDefault(request.regionCode(), "ALL"));
        lines.add("Year Month: " + valueOrDefault(request.yearMonth(), "UNKNOWN"));
        lines.add("Collected Count: " + valueOrDefault(request.collectedCount(), 0L));
        lines.add("Skipped Count: " + valueOrDefault(request.skippedCount(), 0L));
        lines.add("Failed Count: " + valueOrDefault(request.failedCount(), 0));
        lines.add("");
        lines.add("AI Summary");
        lines.addAll(wrap(valueOrDefault(request.summary(), ""), externalFont));
        lines.add("");
        lines.add("Translated Summary");
        lines.addAll(wrap(valueOrDefault(request.translatedSummary(), ""), externalFont));
        lines.add("");
        lines.add("Batch Structure Review");
        lines.add("Strength: collection, AI processing, PDF generation, and result persistence are separated by job, service, and mapper responsibilities.");
        lines.add("Limit: report generation currently uses the latest house-deal collection log and should be extended for historical report selection.");
        lines.add("");
        lines.add("Data Quality Impact");
        lines.add("AI output quality depends on collected, skipped, failed counts and the representativeness of sampled deals.");
        lines.add("");
        lines.add("Operational Improvements");
        lines.add("Recommended next steps: distributed scheduler lock, retry policy for AI API failures, and report template refinement.");
        lines.add("");
        lines.add("Sample Deals");
        List<HouseDealReportRow> deals = request.deals() == null ? List.of() : request.deals();
        if (deals.isEmpty()) {
            lines.add("No sampled deals.");
        } else {
            for (HouseDealReportRow deal : deals) {
                lines.addAll(wrap(formatDeal(deal), externalFont));
            }
        }
        return lines;
    }

    private void writeLines(PDDocument document, PDFont font, List<String> lines) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        PDPageContentStream content = new PDPageContentStream(document, page);
        content.setFont(font, 10F);
        content.beginText();
        content.newLineAtOffset(MARGIN, page.getMediaBox().getHeight() - MARGIN);
        float y = page.getMediaBox().getHeight() - MARGIN;

        for (String line : lines) {
            if (y < MARGIN) {
                content.endText();
                content.close();
                page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                content = new PDPageContentStream(document, page);
                content.setFont(font, 10F);
                content.beginText();
                content.newLineAtOffset(MARGIN, page.getMediaBox().getHeight() - MARGIN);
                y = page.getMediaBox().getHeight() - MARGIN;
            }
            content.showText(line);
            content.newLineAtOffset(0, -LEADING);
            y -= LEADING;
        }
        content.endText();
        content.close();
    }

    private List<String> wrap(String text, boolean externalFont) {
        String normalized = externalFont ? text : toPdfBasicText(text);
        if (normalized.length() <= WRAP_LENGTH) {
            return List.of(normalized);
        }
        List<String> lines = new ArrayList<>();
        for (int start = 0; start < normalized.length(); start += WRAP_LENGTH) {
            lines.add(normalized.substring(start, Math.min(start + WRAP_LENGTH, normalized.length())));
        }
        return lines;
    }

    private String formatDeal(HouseDealReportRow deal) {
        return String.format(
                "- %s, dong=%s, type=%s, dealType=%s, amount=%s, date=%s, area=%s, floor=%s",
                valueOrDefault(deal.getAptName(), ""),
                valueOrDefault(deal.getDongName(), ""),
                valueOrDefault(deal.getHouseType(), ""),
                valueOrDefault(deal.getDealType(), ""),
                valueOrDefault(deal.getDealAmount(), 0),
                valueOrDefault(deal.getDealDate(), ""),
                valueOrDefault(deal.getArea(), ""),
                valueOrDefault(deal.getFloor(), "")
        );
    }

    private String toPdfBasicText(String text) {
        return text == null ? "" : text.replaceAll("[^\\x09\\x0A\\x0D\\x20-\\x7E]", "?");
    }

    private <T> T valueOrDefault(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }
}
