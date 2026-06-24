package com.ssafy.home.batch.report;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "batch.ai-service")
public record BatchAiServiceProperties(
        URI baseUrl,
        Duration timeout
) {
    public BatchAiServiceProperties {
        Objects.requireNonNull(baseUrl, "batch.ai-service.base-url must be configured");
        if (timeout == null || timeout.isZero() || timeout.isNegative()) {
            timeout = Duration.ofSeconds(30);
        }
    }
}
