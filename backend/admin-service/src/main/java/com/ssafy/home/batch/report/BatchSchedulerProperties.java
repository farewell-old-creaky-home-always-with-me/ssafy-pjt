package com.ssafy.home.batch.report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "batch.scheduler")
public record BatchSchedulerProperties(
        boolean enabled,
        @NotBlank String regionCode,
        @Pattern(regexp = "\\d{6}|") String yearMonth,
        @NotBlank String houseType,
        @NotBlank String dealType,
        Long systemMemberId
) {
    public BatchSchedulerProperties {
        if (regionCode == null || regionCode.isBlank()) {
            regionCode = "11110";
        }
        if (yearMonth == null) {
            yearMonth = "";
        }
        if (houseType == null || houseType.isBlank()) {
            houseType = "APARTMENT";
        }
        if (dealType == null || dealType.isBlank()) {
            dealType = "SALE";
        }
        if (systemMemberId == null) {
            systemMemberId = 0L;
        }
    }
}
