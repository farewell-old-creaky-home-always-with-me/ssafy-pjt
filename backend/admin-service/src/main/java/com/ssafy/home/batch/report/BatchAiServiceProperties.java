package com.ssafy.home.batch.report;

import java.net.URI;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "batch.ai-service")
public record BatchAiServiceProperties(
        URI baseUrl,
        Duration timeout
) {
    public BatchAiServiceProperties {
        if (baseUrl == null) {
            baseUrl = URI.create("http://localhost:8083");
        }
        if (timeout == null || timeout.isZero() || timeout.isNegative()) {
            timeout = Duration.ofSeconds(30);
        }
    }
}
