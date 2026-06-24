package com.ssafy.home.batch.config;

import com.ssafy.home.batch.domain.NormalizedCommercialArea;
import com.ssafy.home.batch.listener.BatchCollectionLogListener;
import com.ssafy.home.batch.mapper.BatchCollectionLogMapper;
import com.ssafy.home.batch.mapper.CommercialAreaBatchMapper;
import com.ssafy.home.batch.processor.CommercialAreaProcessor;
import com.ssafy.home.batch.processor.InvalidCommercialAreaException;
import com.ssafy.home.batch.reader.SdscStoreReader;
import com.ssafy.home.batch.writer.CommercialAreaWriter;
import com.ssafy.home.external.sdsc.SdscApiException;
import com.ssafy.home.external.sdsc.SdscProperties;
import com.ssafy.home.external.sdsc.SdscRawStore;
import com.ssafy.home.external.sdsc.SdscStoreClient;
import java.util.List;
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
public class CommercialAreaCollectJobConfig {

    @Bean
    public BatchCollectionLogListener commercialAreaBatchCollectionLogListener(
            BatchCollectionLogMapper mapper
    ) {
        return BatchCollectionLogListener.forCommercialArea(mapper);
    }

    @Bean("commercialAreaCollectJob")
    public Job commercialAreaCollectJob(
            JobRepository jobRepository,
            Step commercialAreaCollectStep,
            BatchCollectionLogListener commercialAreaBatchCollectionLogListener
    ) {
        return new JobBuilder("commercialAreaCollectJob", jobRepository)
                .listener(commercialAreaBatchCollectionLogListener)
                .start(commercialAreaCollectStep)
                .build();
    }

    @Bean
    public Step commercialAreaCollectStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            SdscStoreReader sdscStoreReader,
            CommercialAreaProcessor commercialAreaProcessor,
            CommercialAreaWriter commercialAreaWriter,
            SdscProperties properties
    ) {
        return new StepBuilder("commercialAreaCollectStep", jobRepository)
                .<SdscRawStore, NormalizedCommercialArea>chunk(100, transactionManager)
                .reader(sdscStoreReader)
                .processor(commercialAreaProcessor)
                .writer(commercialAreaWriter)
                .faultTolerant()
                .retryPolicy(sdscRetryPolicy(properties))
                .skip(InvalidCommercialAreaException.class)
                .skipLimit(properties.skipLimit())
                .listener(commercialAreaWriter)
                .build();
    }

    @Bean
    public RetryPolicy sdscRetryPolicy(SdscProperties properties) {
        BinaryExceptionClassifier classifier = new BinaryExceptionClassifier(false) {
            @Override
            public Boolean classify(Throwable throwable) {
                return throwable instanceof SdscApiException exception
                        && exception.retryable();
            }
        };
        return new SimpleRetryPolicy(properties.retryCount() + 1, classifier);
    }

    @Bean
    public SdscStoreReader sdscStoreReader(
            SdscStoreClient client,
            CommercialAreaBatchMapper mapper,
            SdscProperties properties
    ) {
        List<String> sigunguCodes = mapper.findAllSigunguCodes();
        return new SdscStoreReader(client, sigunguCodes, properties.pageSize(), properties.retryCount());
    }

    @Bean
    public CommercialAreaProcessor commercialAreaProcessor() {
        return new CommercialAreaProcessor();
    }

    @Bean
    public CommercialAreaWriter commercialAreaWriter(CommercialAreaBatchMapper mapper) {
        return new CommercialAreaWriter(mapper);
    }
}
