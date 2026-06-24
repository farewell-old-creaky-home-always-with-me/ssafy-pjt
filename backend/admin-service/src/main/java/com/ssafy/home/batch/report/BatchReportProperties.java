package com.ssafy.home.batch.report;

import jakarta.validation.constraints.Positive;
import java.nio.file.Path;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "batch.report")
public record BatchReportProperties(
        Path outputDir,
        String fontPath,
        @Positive int maxDeals
) {
    public BatchReportProperties {
        if (outputDir == null) {
            outputDir = Path.of("./reports/batch");
        }
        if (fontPath == null) {
            fontPath = "";
        }
    }
}
