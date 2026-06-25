package com.ssafy.home.batch.writer;

import com.ssafy.home.batch.domain.NormalizedEnvironment;
import com.ssafy.home.batch.mapper.EnvironmentBatchMapper;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

public class EnvironmentWriter implements ItemWriter<NormalizedEnvironment> {

    private final EnvironmentBatchMapper mapper;
    private StepExecution stepExecution;

    public EnvironmentWriter(EnvironmentBatchMapper mapper) {
        this.mapper = mapper;
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public void write(Chunk<? extends NormalizedEnvironment> chunk) {
        long collected = context().getLong("collectedCount", 0);
        for (NormalizedEnvironment environment : chunk) {
            mapper.upsert(environment);
            collected++;
        }
        context().putLong("collectedCount", collected);
    }

    private ExecutionContext context() {
        if (stepExecution == null) {
            throw new IllegalStateException("StepExecution is not initialized");
        }
        return stepExecution.getExecutionContext();
    }
}
