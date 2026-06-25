package com.ssafy.home.batch.config;

import com.ssafy.home.batch.domain.NormalizedCctv;
import com.ssafy.home.batch.listener.BatchCollectionLogListener;
import com.ssafy.home.batch.mapper.BatchCollectionLogMapper;
import com.ssafy.home.batch.mapper.CctvBatchMapper;
import com.ssafy.home.batch.processor.CctvProcessor;
import com.ssafy.home.batch.processor.InvalidCctvException;
import com.ssafy.home.batch.reader.SeoulCctvReader;
import com.ssafy.home.batch.writer.CctvWriter;
import com.ssafy.home.external.seoul.cctv.SeoulCctvClient;
import com.ssafy.home.external.seoul.cctv.SeoulCctvProperties;
import com.ssafy.home.external.seoul.cctv.SeoulRawCctv;
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
public class CctvCollectJobConfig {

    @Bean
    public BatchCollectionLogListener cctvBatchCollectionLogListener(
            BatchCollectionLogMapper mapper
    ) {
        return BatchCollectionLogListener.forCctv(mapper);
    }

    @Bean("cctvCollectJob")
    public Job cctvCollectJob(
            JobRepository jobRepository,
            Step cctvCollectStep,
            BatchCollectionLogListener cctvBatchCollectionLogListener
    ) {
        return new JobBuilder("cctvCollectJob", jobRepository)
                .listener(cctvBatchCollectionLogListener)
                .start(cctvCollectStep)
                .build();
    }

    @Bean
    public Step cctvCollectStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            SeoulCctvReader seoulCctvReader,
            CctvProcessor cctvProcessor,
            CctvWriter cctvWriter,
            SeoulCctvProperties properties
    ) {
        return new StepBuilder("cctvCollectStep", jobRepository)
                .<SeoulRawCctv, NormalizedCctv>chunk(100, transactionManager)
                .reader(seoulCctvReader)
                .processor(cctvProcessor)
                .writer(cctvWriter)
                .faultTolerant()
                .skip(InvalidCctvException.class)
                .skipLimit(properties.skipLimit())
                .listener(cctvWriter)
                .build();
    }

    @Bean
    @StepScope
    public SeoulCctvReader seoulCctvReader(
            SeoulCctvClient client,
            SeoulCctvProperties properties
    ) {
        return new SeoulCctvReader(client, properties.pageSize(), properties.retryCount());
    }

    @Bean
    public CctvProcessor cctvProcessor() {
        return new CctvProcessor();
    }

    @Bean
    @StepScope
    public CctvWriter cctvWriter(CctvBatchMapper mapper) {
        return new CctvWriter(mapper);
    }
}
