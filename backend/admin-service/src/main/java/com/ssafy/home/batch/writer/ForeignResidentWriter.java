package com.ssafy.home.batch.writer;

import com.ssafy.home.batch.domain.NormalizedForeignResident;
import com.ssafy.home.batch.mapper.DemographicsBatchMapper;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

public class ForeignResidentWriter implements ItemWriter<NormalizedForeignResident> {

    private final DemographicsBatchMapper mapper;
    private StepExecution stepExecution;

    public ForeignResidentWriter(DemographicsBatchMapper mapper) {
        this.mapper = mapper;
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public void write(Chunk<? extends NormalizedForeignResident> chunk) {
        long collected = context().getLong("collectedCount", 0);
        for (NormalizedForeignResident item : chunk) {
            mapper.upsertForeignResident(item);
            collected++;
        }
        context().putLong("collectedCount", collected);
    }

    private ExecutionContext context() {
        return stepExecution.getExecutionContext();
    }
}
