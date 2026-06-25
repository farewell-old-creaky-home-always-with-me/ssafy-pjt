package com.ssafy.home.batch.config;

import com.ssafy.home.batch.domain.NormalizedHousingContent;
import com.ssafy.home.batch.mapper.HousingNewsBatchMapper;
import com.ssafy.home.batch.processor.HousingContentProcessor;
import com.ssafy.home.batch.processor.InvalidHousingContentException;
import com.ssafy.home.batch.reader.HousingContentReader;
import com.ssafy.home.batch.writer.HousingContentWriter;
import com.ssafy.home.external.housing.HousingContentClient;
import com.ssafy.home.external.housing.HousingContentSourceProperties;
import com.ssafy.home.external.housing.HousingRawContent;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableConfigurationProperties(HousingContentSourceProperties.class)
public class HousingNewsCollectJobConfig {

    @Bean("housingNewsCollectJob")
    public Job housingNewsCollectJob(
            JobRepository jobRepository,
            Step housingNewsCollectStep
    ) {
        return new JobBuilder("housingNewsCollectJob", jobRepository)
                .start(housingNewsCollectStep)
                .build();
    }

    @Bean
    public Step housingNewsCollectStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            HousingContentReader housingContentReader,
            HousingContentProcessor housingContentProcessor,
            HousingContentWriter housingContentWriter,
            HousingContentSourceProperties properties
    ) {
        return new StepBuilder("housingNewsCollectStep", jobRepository)
                .<HousingRawContent, NormalizedHousingContent>chunk(50, transactionManager)
                .reader(housingContentReader)
                .processor(housingContentProcessor)
                .writer(housingContentWriter)
                .faultTolerant()
                .skip(InvalidHousingContentException.class)
                .skipLimit(properties.skipLimit())
                .listener(housingContentWriter)
                .build();
    }

    @Bean
    @StepScope
    public HousingContentReader housingContentReader(
            HousingContentClient client,
            HousingContentSourceProperties properties
    ) {
        return new HousingContentReader(client, properties.sources());
    }

    @Bean
    public HousingContentProcessor housingContentProcessor() {
        return new HousingContentProcessor();
    }

    @Bean
    public HousingContentWriter housingContentWriter(HousingNewsBatchMapper mapper) {
        return new HousingContentWriter(mapper);
    }
}
