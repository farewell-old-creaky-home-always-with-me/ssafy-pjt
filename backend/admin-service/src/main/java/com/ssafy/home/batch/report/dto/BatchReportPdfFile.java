package com.ssafy.home.batch.report.dto;

import java.nio.file.Path;

public record BatchReportPdfFile(
        String fileName,
        Path path
) {
}
