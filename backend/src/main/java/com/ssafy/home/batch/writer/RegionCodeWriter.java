package com.ssafy.home.batch.writer;

import com.ssafy.home.batch.domain.NormalizedRegionCode;
import com.ssafy.home.batch.mapper.RegionBatchMapper;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

public class RegionCodeWriter implements ItemWriter<NormalizedRegionCode> {

    private final RegionBatchMapper mapper;
    private StepExecution stepExecution;

    public RegionCodeWriter(RegionBatchMapper mapper) {
        this.mapper = mapper;
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public void write(Chunk<? extends NormalizedRegionCode> chunk) {
        long collected = context().getLong("collectedCount", 0);
        for (NormalizedRegionCode regionCode : chunk) {
            mapper.upsertRegionCode(regionCode);
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
