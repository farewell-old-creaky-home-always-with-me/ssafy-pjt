package com.ssafy.home.batch.writer;

import com.ssafy.home.batch.domain.NormalizedHousingContent;
import com.ssafy.home.batch.domain.NormalizedHousingInfo;
import com.ssafy.home.batch.domain.NormalizedHousingNews;
import com.ssafy.home.batch.mapper.HousingNewsBatchMapper;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

public class HousingContentWriter implements ItemWriter<NormalizedHousingContent> {

    private final HousingNewsBatchMapper mapper;
    private StepExecution stepExecution;

    public HousingContentWriter(HousingNewsBatchMapper mapper) {
        this.mapper = mapper;
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public void write(Chunk<? extends NormalizedHousingContent> chunk) {
        long collected = context().getLong("collectedCount", 0);
        for (NormalizedHousingContent content : chunk) {
            if (content instanceof NormalizedHousingNews news) {
                mapper.upsertNews(news);
            } else if (content instanceof NormalizedHousingInfo info) {
                mapper.upsertInfo(info);
            } else {
                throw new IllegalArgumentException("Unsupported housing content type: " + content.getClass());
            }
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
