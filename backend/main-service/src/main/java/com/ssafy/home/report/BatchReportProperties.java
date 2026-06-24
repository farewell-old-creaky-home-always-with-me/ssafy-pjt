package com.ssafy.home.report;

import java.nio.file.Path;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "batch.report")
public record BatchReportProperties(Path outputDir) {

    public BatchReportProperties {
        if (outputDir == null) {
            outputDir = Path.of("./reports/batch");
        }
    }
}
