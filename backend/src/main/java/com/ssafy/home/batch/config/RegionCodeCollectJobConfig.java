package com.ssafy.home.batch.config;

import com.ssafy.home.batch.domain.NormalizedRegionCode;
import com.ssafy.home.batch.listener.BatchCollectionLogListener;
import com.ssafy.home.batch.mapper.BatchCollectionLogMapper;
import com.ssafy.home.batch.mapper.RegionBatchMapper;
import com.ssafy.home.batch.processor.InvalidRegionCodeException;
import com.ssafy.home.batch.processor.RegionCodeProcessor;
import com.ssafy.home.batch.reader.VworldRegionReader;
import com.ssafy.home.batch.writer.RegionCodeWriter;
import com.ssafy.home.external.vworld.VworldApiException;
import com.ssafy.home.external.vworld.VworldLegalRegionClient;
import com.ssafy.home.external.vworld.VworldProperties;
import com.ssafy.home.external.vworld.VworldRawRegion;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class RegionCodeCollectJobConfig {

    @Bean
    public BatchCollectionLogListener regionBatchCollectionLogListener(
            BatchCollectionLogMapper mapper
    ) {
        return BatchCollectionLogListener.forRegionCode(mapper);
    }

    @Bean("regionCodeCollectJob")
    public Job regionCodeCollectJob(
            JobRepository jobRepository,
            Step regionCodeCollectStep,
            BatchCollectionLogListener regionBatchCollectionLogListener
    ) {
        return new JobBuilder("regionCodeCollectJob", jobRepository)
                .listener(regionBatchCollectionLogListener)
                .start(regionCodeCollectStep)
                .build();
    }

    @Bean
    public Step regionCodeCollectStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            VworldRegionReader vworldRegionReader,
            RegionCodeProcessor regionCodeProcessor,
            RegionCodeWriter regionCodeWriter,
            VworldProperties properties
    ) {
        return new StepBuilder("regionCodeCollectStep", jobRepository)
                .<VworldRawRegion, NormalizedRegionCode>chunk(100, transactionManager)
                .reader(vworldRegionReader)
                .processor(regionCodeProcessor)
                .writer(regionCodeWriter)
                .faultTolerant()
                .retryPolicy(vworldRetryPolicy(properties))
                .skip(InvalidRegionCodeException.class)
                .skipLimit(properties.skipLimit())
                .listener(regionCodeWriter)
                .build();
    }

    @Bean
    public RetryPolicy vworldRetryPolicy(VworldProperties properties) {
        BinaryExceptionClassifier classifier = new BinaryExceptionClassifier(false) {
            @Override
            public Boolean classify(Throwable throwable) {
                return throwable instanceof VworldApiException exception
                        && exception.retryable();
            }
        };
        return new SimpleRetryPolicy(properties.retryCount() + 1, classifier);
    }

    @Bean
    public VworldRegionReader vworldRegionReader(
            VworldLegalRegionClient client,
            VworldProperties properties
    ) {
        return new VworldRegionReader(client, properties);
    }

    @Bean
    public RegionCodeProcessor regionCodeProcessor() {
        return new RegionCodeProcessor();
    }

    @Bean
    public RegionCodeWriter regionCodeWriter(RegionBatchMapper mapper) {
        return new RegionCodeWriter(mapper);
    }
}
