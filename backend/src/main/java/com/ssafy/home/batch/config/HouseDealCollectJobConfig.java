package com.ssafy.home.batch.config;

import com.ssafy.home.batch.domain.HouseType;
import com.ssafy.home.batch.domain.NormalizedHouseDeal;
import com.ssafy.home.batch.listener.BatchCollectionLogListener;
import com.ssafy.home.batch.mapper.BatchCollectionLogMapper;
import com.ssafy.home.batch.mapper.HouseDealBatchMapper;
import com.ssafy.home.batch.processor.HouseDealProcessor;
import com.ssafy.home.batch.processor.InvalidHouseDealException;
import com.ssafy.home.batch.reader.MolitHouseDealReader;
import com.ssafy.home.batch.writer.HouseDealWriter;
import com.ssafy.home.external.molit.MolitApiException;
import com.ssafy.home.external.molit.MolitHouseDealClient;
import com.ssafy.home.external.molit.MolitProperties;
import com.ssafy.home.external.molit.MolitRawHouseDeal;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class HouseDealCollectJobConfig {

    @Bean
    public BatchCollectionLogListener houseDealBatchCollectionLogListener(
            BatchCollectionLogMapper mapper
    ) {
        return BatchCollectionLogListener.forHouseDeal(mapper);
    }

    @Bean("houseDealCollectJob")
    public Job houseDealCollectJob(
            JobRepository jobRepository,
            Step houseDealCollectStep,
            BatchCollectionLogListener houseDealBatchCollectionLogListener
    ) {
        return new JobBuilder("houseDealCollectJob", jobRepository)
                .listener(houseDealBatchCollectionLogListener)
                .start(houseDealCollectStep)
                .build();
    }

    @Bean
    public Step houseDealCollectStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            MolitHouseDealReader houseDealReader,
            HouseDealProcessor houseDealProcessor,
            HouseDealWriter houseDealWriter,
            MolitProperties properties
    ) {
        return new StepBuilder("houseDealCollectStep", jobRepository)
                .<MolitRawHouseDeal, NormalizedHouseDeal>chunk(100, transactionManager)
                .reader(houseDealReader)
                .processor(houseDealProcessor)
                .writer(houseDealWriter)
                .faultTolerant()
                .retryPolicy(molitRetryPolicy(properties))
                .skip(InvalidHouseDealException.class)
                .skipLimit(Integer.MAX_VALUE)
                .listener(houseDealWriter)
                .build();
    }

    @Bean
    public RetryPolicy molitRetryPolicy(MolitProperties properties) {
        BinaryExceptionClassifier classifier = new BinaryExceptionClassifier(false) {
            @Override
            public Boolean classify(Throwable throwable) {
                return throwable instanceof MolitApiException exception
                        && exception.retryable();
            }
        };
        return new SimpleRetryPolicy(properties.retryCount() + 1, classifier);
    }

    @Bean
    @StepScope
    public MolitHouseDealReader houseDealReader(
            List<MolitHouseDealClient> clients,
            @Value("#{jobParameters['regionCode']}") String regionCode,
            @Value("#{jobParameters['yearMonth']}") String yearMonth,
            @Value("#{jobParameters['houseType']}") String houseType
    ) {
        HouseType type = HouseType.from(houseType);
        MolitHouseDealClient client = clients.stream()
                .filter(candidate -> candidate.supports(type))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No MOLIT client supports " + type
                ));
        return new MolitHouseDealReader(client, regionCode, yearMonth);
    }

    @Bean
    @StepScope
    public HouseDealProcessor houseDealProcessor(
            HouseDealBatchMapper mapper,
            @Value("#{jobParameters['houseType']}") String houseType
    ) {
        return new HouseDealProcessor(mapper, HouseType.from(houseType));
    }

    @Bean
    @StepScope
    public HouseDealWriter houseDealWriter(HouseDealBatchMapper mapper) {
        return new HouseDealWriter(mapper);
    }
}
