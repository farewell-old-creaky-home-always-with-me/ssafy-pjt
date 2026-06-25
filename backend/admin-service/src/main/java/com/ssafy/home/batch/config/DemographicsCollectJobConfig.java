package com.ssafy.home.batch.config;

import com.ssafy.home.batch.domain.NormalizedForeignResident;
import com.ssafy.home.batch.domain.NormalizedPopulation;
import com.ssafy.home.batch.listener.BatchCollectionLogListener;
import com.ssafy.home.batch.mapper.BatchCollectionLogMapper;
import com.ssafy.home.batch.mapper.DemographicsBatchMapper;
import com.ssafy.home.batch.processor.ForeignResidentProcessor;
import com.ssafy.home.batch.processor.InvalidForeignResidentException;
import com.ssafy.home.batch.processor.InvalidPopulationException;
import com.ssafy.home.batch.processor.PopulationProcessor;
import com.ssafy.home.batch.reader.SeoulForeignResidentReader;
import com.ssafy.home.batch.reader.SeoulPopulationReader;
import com.ssafy.home.batch.writer.ForeignResidentWriter;
import com.ssafy.home.batch.writer.PopulationWriter;
import com.ssafy.home.external.seoul.demographics.SeoulDemographicsClient;
import com.ssafy.home.external.seoul.demographics.SeoulDemographicsProperties;
import com.ssafy.home.external.seoul.demographics.SeoulRawForeignResident;
import com.ssafy.home.external.seoul.demographics.SeoulRawPopulation;
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
public class DemographicsCollectJobConfig {

    @Bean
    public BatchCollectionLogListener demographicsBatchCollectionLogListener(
            BatchCollectionLogMapper mapper
    ) {
        return BatchCollectionLogListener.forDemographics(mapper);
    }

    @Bean("demographicsCollectJob")
    public Job demographicsCollectJob(
            JobRepository jobRepository,
            Step populationCollectStep,
            Step foreignResidentCollectStep,
            BatchCollectionLogListener demographicsBatchCollectionLogListener
    ) {
        return new JobBuilder("demographicsCollectJob", jobRepository)
                .listener(demographicsBatchCollectionLogListener)
                .start(populationCollectStep)
                .next(foreignResidentCollectStep)
                .build();
    }

    @Bean
    public Step populationCollectStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            SeoulPopulationReader seoulPopulationReader,
            PopulationProcessor populationProcessor,
            PopulationWriter populationWriter,
            SeoulDemographicsProperties properties
    ) {
        return new StepBuilder("populationCollectStep", jobRepository)
                .<SeoulRawPopulation, NormalizedPopulation>chunk(100, transactionManager)
                .reader(seoulPopulationReader)
                .processor(populationProcessor)
                .writer(populationWriter)
                .faultTolerant()
                .skip(InvalidPopulationException.class)
                .skipLimit(properties.skipLimit())
                .listener(populationWriter)
                .build();
    }

    @Bean
    public Step foreignResidentCollectStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            SeoulForeignResidentReader seoulForeignResidentReader,
            ForeignResidentProcessor foreignResidentProcessor,
            ForeignResidentWriter foreignResidentWriter,
            SeoulDemographicsProperties properties
    ) {
        return new StepBuilder("foreignResidentCollectStep", jobRepository)
                .<SeoulRawForeignResident, NormalizedForeignResident>chunk(100, transactionManager)
                .reader(seoulForeignResidentReader)
                .processor(foreignResidentProcessor)
                .writer(foreignResidentWriter)
                .faultTolerant()
                .skip(InvalidForeignResidentException.class)
                .skipLimit(properties.skipLimit())
                .listener(foreignResidentWriter)
                .build();
    }

    @Bean
    @StepScope
    public SeoulPopulationReader seoulPopulationReader(
            SeoulDemographicsClient client, SeoulDemographicsProperties properties
    ) {
        return new SeoulPopulationReader(client, properties.pageSize(), properties.retryCount());
    }

    @Bean
    public PopulationProcessor populationProcessor() {
        return new PopulationProcessor();
    }

    @Bean
    @StepScope
    public PopulationWriter populationWriter(DemographicsBatchMapper mapper) {
        return new PopulationWriter(mapper);
    }

    @Bean
    @StepScope
    public SeoulForeignResidentReader seoulForeignResidentReader(
            SeoulDemographicsClient client, SeoulDemographicsProperties properties
    ) {
        return new SeoulForeignResidentReader(client, properties.pageSize(), properties.retryCount());
    }

    @Bean
    public ForeignResidentProcessor foreignResidentProcessor() {
        return new ForeignResidentProcessor();
    }

    @Bean
    @StepScope
    public ForeignResidentWriter foreignResidentWriter(DemographicsBatchMapper mapper) {
        return new ForeignResidentWriter(mapper);
    }
}
