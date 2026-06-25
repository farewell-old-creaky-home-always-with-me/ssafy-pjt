package com.ssafy.home.external.seoul;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "seoul.environment")
public record SeoulEnvironmentProperties(
        @NotNull URI baseUrl,
        @NotBlank String serviceKey,
        @Positive int pageSize,
        @NotNull Duration timeout,
        @PositiveOrZero int retryCount,
        @Positive int skipLimit,
        @Valid @NotEmpty List<Dataset> datasets
) {
    public SeoulEnvironmentProperties {
        if (timeout != null && (timeout.isZero() || timeout.isNegative())) {
            throw new IllegalArgumentException("timeout must be positive");
        }
        if (baseUrl != null && !"https".equalsIgnoreCase(baseUrl.getScheme())) {
            throw new IllegalArgumentException("seoul.environment.base-url must use HTTPS");
        }
    }

    public record Dataset(
            @NotBlank String key,
            @NotBlank String category,
            @NotBlank String serviceName,
            @NotBlank String itemNameField,
            String valueField,
            String unitField,
            String measuredDateField,
            @NotBlank String latitudeField,
            @NotBlank String longitudeField
    ) {
    }
}
