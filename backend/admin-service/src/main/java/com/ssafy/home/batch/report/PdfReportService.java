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
import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.util.Matrix;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PdfReportService {

    private static final DateTimeFormatter FILE_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final float MARGIN = 50F;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();

    private static final float TITLE_SIZE = 20F;
    private static final float HEADING_SIZE = 12F;
    private static final float BODY_SIZE = 10F;
    private static final float SMALL_SIZE = 9F;

    private static final float TITLE_LEADING = 26F;
    private static final float HEADING_LEADING = 18F;
    private static final float BODY_LEADING = 15F;

    private static final int WRAP_CHARS = 80;

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

            boolean isTtc = properties.fontPath().toLowerCase().endsWith(".ttc");
            try (TrueTypeCollection ttc = isTtc ? new TrueTypeCollection(Path.of(properties.fontPath()).toFile()) : null;
                 PDDocument document = new PDDocument()) {
                PDFont font = loadFont(document, ttc);
                writePdf(document, font, request);
                document.save(filePath.toFile());
            }
            return new PdfReportResult(fileName, filePath.toString());
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to generate batch report PDF", exception);
        }
    }

    private void writePdf(PDDocument document, PDFont font, PdfReportRequest request) throws IOException {
        Ctx ctx = new Ctx(document, font);

        // 제목
        ctx.text("AI 배치 리포트", TITLE_SIZE, TITLE_LEADING);
        ctx.text(
            "리포트 #" + request.reportId()
            + "  |  " + formatYearMonth(valueOrDefault(request.yearMonth(), ""))
            + "  |  지역코드 " + valueOrDefault(request.regionCode(), "전체"),
            SMALL_SIZE, BODY_LEADING
        );
        ctx.space(6);
        ctx.hline(1.5f);
        ctx.space(14);

        // 수집 현황
        ctx.heading("수집 현황");
        ctx.text(
            String.format("수집 %d건   |   제외 %d건   |   실패 %d건",
                valueOrDefault(request.collectedCount(), 0L),
                valueOrDefault(request.skippedCount(), 0L),
                valueOrDefault(request.failedCount(), 0)),
            BODY_SIZE, BODY_LEADING
        );
        ctx.space(14);
        ctx.hline(0.5f);
        ctx.space(14);

        // AI 요약 (한국어)
        ctx.heading("AI 요약");
        ctx.body(valueOrDefault(request.summary(), "내용 없음"));
        ctx.space(14);
        ctx.hline(0.5f);
        ctx.space(14);

        // AI 요약 (영문)
        ctx.heading("AI Summary (English)");
        ctx.body(valueOrDefault(request.translatedSummary(), "No content"));
        ctx.space(14);
        ctx.hline(0.5f);
        ctx.space(14);

        // 주요 거래 샘플
        ctx.heading("주요 거래 샘플");
        List<HouseDealReportRow> deals = request.deals() == null ? List.of() : request.deals();
        if (deals.isEmpty()) {
            ctx.text("샘플 거래 데이터 없음", BODY_SIZE, BODY_LEADING);
        } else {
            for (HouseDealReportRow deal : deals) {
                ctx.text(formatDealLine(deal), SMALL_SIZE, BODY_LEADING);
            }
        }

        ctx.finish();
    }

    private String formatYearMonth(String ym) {
        if (ym == null || ym.length() != 6) return ym == null ? "" : ym;
        return ym.substring(0, 4) + "년 " + ym.substring(4) + "월";
    }

    private String formatDealLine(HouseDealReportRow deal) {
        String name = valueOrDefault(deal.getAptName(), "-");
        String dong = valueOrDefault(deal.getDongName(), "-");
        String area = deal.getArea() != null ? deal.getArea().toPlainString() + "㎡" : "-";
        String amount = deal.getDealAmount() != null
                ? String.format("%,d만원", deal.getDealAmount()) : "-";
        String date = deal.getDealDate() != null ? deal.getDealDate().toString() : "-";
        return String.format("• %s (%s)  %s  %s  %s", name, dong, area, amount, date);
    }

    private List<String> wrap(String text) {
        if (text == null || text.isEmpty()) return List.of();
        List<String> lines = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + WRAP_CHARS, text.length());
            if (end < text.length()) {
                int lastSpace = text.lastIndexOf(' ', end);
                if (lastSpace > start) end = lastSpace + 1;
            }
            lines.add(text.substring(start, end).stripTrailing());
            start = end;
            while (start < text.length() && text.charAt(start) == ' ') start++;
        }
        return lines;
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

    private PDFont loadFont(PDDocument document, TrueTypeCollection ttc) throws IOException {
        if (!hasExternalFont()) {
            throw new IllegalStateException("Batch report PDF font must be configured with a readable font file");
        }
        if (ttc != null) {
            TrueTypeFont[] holder = {null};
            ttc.processAllFonts(font -> {
                if (holder[0] == null) {
                    holder[0] = font;
                }
            });
            if (holder[0] == null) {
                throw new IOException("No fonts found in TTC file: " + properties.fontPath());
            }
            return PDType0Font.load(document, holder[0], true);
        }
        return PDType0Font.load(document, Path.of(properties.fontPath()).toFile());
    }

    private boolean hasExternalFont() {
        return StringUtils.hasText(properties.fontPath()) && Files.isRegularFile(Path.of(properties.fontPath()));
    }

    private <T> T valueOrDefault(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    private class Ctx {
        private final PDDocument document;
        private final PDFont font;
        private PDPageContentStream cs;
        private float y;
        private boolean inText = false;

        Ctx(PDDocument document, PDFont font) throws IOException {
            this.document = document;
            this.font = font;
            newPage();
        }

        private void newPage() throws IOException {
            if (cs != null) {
                exitText();
                cs.close();
            }
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            cs = new PDPageContentStream(document, page);
            y = PAGE_HEIGHT - MARGIN;
        }

        void text(String line, float size, float leading) throws IOException {
            if (y - leading < MARGIN) newPage();
            if (!inText) {
                cs.beginText();
                inText = true;
            }
            cs.setFont(font, size);
            cs.setTextMatrix(Matrix.getTranslateInstance(MARGIN, y));
            cs.showText(line);
            y -= leading;
        }

        void heading(String title) throws IOException {
            if (y - HEADING_LEADING < MARGIN) newPage();
            exitText();
            cs.beginText();
            cs.setFont(font, HEADING_SIZE);
            cs.setTextMatrix(Matrix.getTranslateInstance(MARGIN, y));
            cs.showText(title);
            cs.endText();
            y -= HEADING_LEADING;
            space(3);
        }

        void body(String text) throws IOException {
            for (String line : wrap(text)) {
                text(line, BODY_SIZE, BODY_LEADING);
            }
        }

        void hline(float width) throws IOException {
            exitText();
            cs.setLineWidth(width);
            cs.moveTo(MARGIN, y);
            cs.lineTo(PAGE_WIDTH - MARGIN, y);
            cs.stroke();
        }

        void space(float pts) throws IOException {
            exitText();
            y -= pts;
        }

        void finish() throws IOException {
            exitText();
            cs.close();
        }

        private void exitText() throws IOException {
            if (inText) {
                cs.endText();
                inText = false;
            }
        }
    }
}
