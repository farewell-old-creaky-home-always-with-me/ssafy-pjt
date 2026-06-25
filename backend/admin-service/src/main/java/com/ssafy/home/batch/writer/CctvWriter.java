package com.ssafy.home.batch.writer;

import com.ssafy.home.batch.domain.NormalizedCctv;
import com.ssafy.home.batch.mapper.CctvBatchMapper;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

public class CctvWriter implements ItemWriter<NormalizedCctv> {

    private final CctvBatchMapper mapper;
    private StepExecution stepExecution;

    public CctvWriter(CctvBatchMapper mapper) {
        this.mapper = mapper;
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public void write(Chunk<? extends NormalizedCctv> chunk) {
        long collected = context().getLong("collectedCount", 0);
        for (NormalizedCctv cctv : chunk) {
            mapper.upsert(cctv);
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
