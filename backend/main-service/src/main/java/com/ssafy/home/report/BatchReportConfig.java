package com.ssafy.home.report;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BatchReportProperties.class)
public class BatchReportConfig {
}
