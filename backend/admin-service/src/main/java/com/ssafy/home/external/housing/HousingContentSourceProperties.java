package com.ssafy.home.external.housing;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "housing.content")
public record HousingContentSourceProperties(
        Duration timeout,
        int skipLimit,
        List<Source> sources
) {
    public HousingContentSourceProperties {
        if (timeout == null) {
            timeout = Duration.ofSeconds(10);
        }
        if (skipLimit <= 0) {
            skipLimit = 100;
        }
        if (sources == null) {
            sources = List.of();
        }
    }

    public record Source(
            String name,
            URI url,
            boolean information,
            String type
    ) {
    }
}
