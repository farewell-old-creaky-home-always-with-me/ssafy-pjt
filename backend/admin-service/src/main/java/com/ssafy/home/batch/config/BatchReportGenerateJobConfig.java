package com.ssafy.home.batch.config;

import com.ssafy.home.batch.report.BatchReportService;
import com.ssafy.home.batch.report.BatchReportTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchReportGenerateJobConfig {

    @Bean("batchReportGenerateJob")
    public Job batchReportGenerateJob(JobRepository jobRepository, Step batchReportGenerateStep) {
        return new JobBuilder("batchReportGenerateJob", jobRepository)
                .start(batchReportGenerateStep)
                .build();
    }

    @Bean
    public Step batchReportGenerateStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            BatchReportService batchReportService
    ) {
        return new StepBuilder("batchReportGenerateStep", jobRepository)
                .tasklet(new BatchReportTasklet(batchReportService), transactionManager)
                .build();
    }
}
