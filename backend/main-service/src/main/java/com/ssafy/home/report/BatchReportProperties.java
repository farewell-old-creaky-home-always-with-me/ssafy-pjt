package com.ssafy.home.report;

import java.nio.file.Path;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "batch.report")
public record BatchReportProperties(Path outputDir) {

    public BatchReportProperties {
        Objects.requireNonNull(outputDir, "batch.report.output-dir must be configured");
        if (!outputDir.isAbsolute()) {
            throw new IllegalStateException("batch.report.output-dir must be an absolute path");
        }
    }
}
