package com.ssafy.home.batch.config;

import com.ssafy.home.batch.domain.NormalizedEnvironment;
import com.ssafy.home.batch.listener.BatchCollectionLogListener;
import com.ssafy.home.batch.mapper.BatchCollectionLogMapper;
import com.ssafy.home.batch.mapper.EnvironmentBatchMapper;
import com.ssafy.home.batch.processor.EnvironmentProcessor;
import com.ssafy.home.batch.processor.InvalidEnvironmentException;
import com.ssafy.home.batch.reader.SeoulEnvironmentReader;
import com.ssafy.home.batch.writer.EnvironmentWriter;
import com.ssafy.home.external.seoul.SeoulEnvironmentClient;
import com.ssafy.home.external.seoul.SeoulEnvironmentProperties;
import com.ssafy.home.external.seoul.SeoulRawEnvironment;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class EnvironmentCollectJobConfig {

    @Bean
    public BatchCollectionLogListener environmentBatchCollectionLogListener(
            BatchCollectionLogMapper mapper
    ) {
        return BatchCollectionLogListener.forEnvironment(mapper);
    }

    @Bean("environmentCollectJob")
    public Job environmentCollectJob(
            JobRepository jobRepository,
            Step environmentCollectStep,
            BatchCollectionLogListener environmentBatchCollectionLogListener
    ) {
        return new JobBuilder("environmentCollectJob", jobRepository)
                .listener(environmentBatchCollectionLogListener)
                .start(environmentCollectStep)
                .build();
    }

    @Bean
    public Step environmentCollectStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            SeoulEnvironmentReader seoulEnvironmentReader,
            EnvironmentProcessor environmentProcessor,
            EnvironmentWriter environmentWriter,
            SeoulEnvironmentProperties properties
    ) {
        return new StepBuilder("environmentCollectStep", jobRepository)
                .<SeoulRawEnvironment, NormalizedEnvironment>chunk(100, transactionManager)
                .reader(seoulEnvironmentReader)
                .processor(environmentProcessor)
                .writer(environmentWriter)
                .faultTolerant()
                .skip(InvalidEnvironmentException.class)
                .skipLimit(properties.skipLimit())
                .listener(environmentWriter)
                .build();
    }

    @Bean
    @StepScope
    public SeoulEnvironmentReader seoulEnvironmentReader(
            SeoulEnvironmentClient client,
            SeoulEnvironmentProperties properties
    ) {
        List<String> datasetKeys = properties.datasets().stream()
                .map(SeoulEnvironmentProperties.Dataset::key)
                .toList();
        return new SeoulEnvironmentReader(client, datasetKeys, properties.pageSize(), properties.retryCount());
    }

    @Bean
    public EnvironmentProcessor environmentProcessor() {
        return new EnvironmentProcessor();
    }

    @Bean
    public EnvironmentWriter environmentWriter(EnvironmentBatchMapper mapper) {
        return new EnvironmentWriter(mapper);
    }
}
