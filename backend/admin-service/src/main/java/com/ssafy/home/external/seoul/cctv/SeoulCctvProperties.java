package com.ssafy.home.external.seoul.cctv;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.net.URI;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "seoul.cctv")
public record SeoulCctvProperties(
        @NotNull URI baseUrl,
        @NotBlank String serviceKey,
        @NotBlank String serviceName,
        @NotBlank String purposeField,
        @NotBlank String cameraCountField,
        String addressField,
        @NotBlank String latitudeField,
        @NotBlank String longitudeField,
        @Positive int pageSize,
        @NotNull Duration timeout,
        @PositiveOrZero int retryCount,
        @Positive int skipLimit
) {
    public SeoulCctvProperties {
        if (timeout != null && (timeout.isZero() || timeout.isNegative())) {
            throw new IllegalArgumentException("timeout must be positive");
        }
        if (baseUrl != null && !"https".equalsIgnoreCase(baseUrl.getScheme())) {
            throw new IllegalArgumentException("seoul.cctv.base-url must use HTTPS");
        }
    }
}
