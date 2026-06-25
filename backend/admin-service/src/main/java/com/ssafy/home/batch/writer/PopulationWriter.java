package com.ssafy.home.batch.writer;

import com.ssafy.home.batch.domain.NormalizedPopulation;
import com.ssafy.home.batch.mapper.DemographicsBatchMapper;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

public class PopulationWriter implements ItemWriter<NormalizedPopulation> {

    private final DemographicsBatchMapper mapper;
    private StepExecution stepExecution;

    public PopulationWriter(DemographicsBatchMapper mapper) {
        this.mapper = mapper;
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public void write(Chunk<? extends NormalizedPopulation> chunk) {
        long collected = context().getLong("collectedCount", 0);
        for (NormalizedPopulation item : chunk) {
            mapper.upsertPopulation(item);
            collected++;
        }
        context().putLong("collectedCount", collected);
    }

    private ExecutionContext context() {
        return stepExecution.getExecutionContext();
    }
}
