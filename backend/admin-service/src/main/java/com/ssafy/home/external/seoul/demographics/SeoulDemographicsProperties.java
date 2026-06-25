package com.ssafy.home.external.seoul.demographics;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.net.URI;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "seoul.demographics")
public record SeoulDemographicsProperties(
        @NotNull URI baseUrl,
        @NotBlank String serviceKey,
        @Positive int pageSize,
        @NotNull Duration timeout,
        @PositiveOrZero int retryCount,
        @Positive int skipLimit,
        @Valid @NotNull Population population,
        @Valid @NotNull ForeignResident foreignResident
) {
    public record Population(
            @NotBlank String serviceName,
            @NotBlank String sidoField,
            @NotBlank String sigunguField,
            @NotBlank String dongField,
            @NotBlank String totalPopulationField,
            @NotBlank String householdField,
            @NotBlank String seniorField,
            @NotBlank String referenceDateField
    ) {}

    public record ForeignResident(
            @NotBlank String serviceName,
            @NotBlank String sidoField,
            @NotBlank String sigunguField,
            @NotBlank String dongField,
            @NotBlank String foreignCountField,
            @NotBlank String referenceDateField
    ) {}
}
