package com.ssafy.home.global.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.admin.csrf")
public record AdminCsrfProperties(
        List<String> allowedOrigins
) {

    public AdminCsrfProperties {
        allowedOrigins = allowedOrigins == null ? List.of() : List.copyOf(allowedOrigins);
    }
}
