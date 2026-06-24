package com.ssafy.home.batch.writer;

import com.ssafy.home.batch.domain.NormalizedCommercialArea;
import com.ssafy.home.batch.mapper.CommercialAreaBatchMapper;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

public class CommercialAreaWriter implements ItemWriter<NormalizedCommercialArea> {

    private final CommercialAreaBatchMapper mapper;
    private StepExecution stepExecution;

    public CommercialAreaWriter(CommercialAreaBatchMapper mapper) {
        this.mapper = mapper;
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public void write(Chunk<? extends NormalizedCommercialArea> chunk) {
        long collected = context().getLong("collectedCount", 0);
        for (NormalizedCommercialArea area : chunk) {
            mapper.upsert(area);
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
