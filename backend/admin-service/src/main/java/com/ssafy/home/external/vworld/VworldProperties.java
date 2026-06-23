package com.ssafy.home.external.vworld;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.net.URI;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "vworld")
public record VworldProperties(
        @NotNull URI dataUrl,
        @NotBlank String apiKey,
        @NotBlank String domain,
        @NotBlank String legalEmdLayer,
        @Positive int pageSize,
        @NotNull Duration timeout,
        @PositiveOrZero int retryCount,
        @PositiveOrZero int sidoRetryCount,
        @Positive int skipLimit
) {
    public VworldProperties {
        if (timeout != null && (timeout.isZero() || timeout.isNegative())) {
            throw new IllegalArgumentException("timeout must be positive");
        }
    }
}
