package com.ssafy.home.batch.report;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class BatchReportTasklet implements Tasklet {

    private final BatchReportService batchReportService;

    public BatchReportTasklet(BatchReportService batchReportService) {
        this.batchReportService = batchReportService;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        Long jobExecutionId = contribution.getStepExecution().getJobExecutionId();
        batchReportService.generateLatestHouseDealReport(jobExecutionId);
        return RepeatStatus.FINISHED;
    }
}
