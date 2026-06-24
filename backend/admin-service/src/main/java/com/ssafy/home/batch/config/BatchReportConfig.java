package com.ssafy.home.batch.config;

import com.ssafy.home.batch.report.BatchAiServiceProperties;
import com.ssafy.home.batch.report.BatchReportProperties;
import com.ssafy.home.batch.report.BatchSchedulerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties({
        BatchAiServiceProperties.class,
        BatchReportProperties.class,
        BatchSchedulerProperties.class
})
public class BatchReportConfig {
}
