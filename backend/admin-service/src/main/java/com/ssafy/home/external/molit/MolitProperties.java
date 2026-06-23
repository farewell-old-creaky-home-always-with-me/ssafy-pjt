package com.ssafy.home.external.molit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.net.URI;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "molit")
public record MolitProperties(
        @NotNull URI apartmentSaleUrl,
        @NotNull URI multiFamilySaleUrl,
        @NotBlank String serviceKey,
        @Positive int pageSize,
        @NotNull Duration timeout,
        @PositiveOrZero int retryCount,
        @Positive int skipLimit
) {
    public MolitProperties {
        if (timeout != null && (timeout.isZero() || timeout.isNegative())) {
            throw new IllegalArgumentException("timeout must be positive");
        }
    }
}
