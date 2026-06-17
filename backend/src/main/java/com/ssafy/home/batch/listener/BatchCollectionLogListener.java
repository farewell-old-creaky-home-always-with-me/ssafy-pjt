package com.ssafy.home.batch.listener;

import com.ssafy.home.batch.domain.BatchCollectionLog;
import com.ssafy.home.batch.mapper.BatchCollectionLogMapper;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;

public class BatchCollectionLogListener implements JobExecutionListener {

    private final BatchCollectionLogMapper mapper;

    public BatchCollectionLogListener(BatchCollectionLogMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        JobParameters parameters = jobExecution.getJobParameters();
        long collectedCount = jobExecution.getStepExecutions().stream()
                .mapToLong(this::collectedCount)
                .sum();
        long skippedCount = jobExecution.getStepExecutions().stream()
                .mapToLong(this::skippedCount)
                .sum();
        int failedCount = jobExecution.getStatus() == BatchStatus.FAILED
                ? jobExecution.getAllFailureExceptions().size()
                : 0;
        String jobName = jobExecution.getJobInstance() == null
                ? "houseDealCollectJob"
                : jobExecution.getJobInstance().getJobName();

        mapper.insert(new BatchCollectionLog(
                jobExecution.getId(),
                jobName,
                "HOUSE_DEAL",
                parameters.getString("regionCode"),
                parameters.getString("yearMonth"),
                parameters.getString("houseType"),
                parameters.getString("dealType"),
                collectedCount,
                skippedCount,
                failedCount,
                jobExecution.getStatus().name(),
                jobExecution.getStartTime(),
                jobExecution.getEndTime()
        ));
    }

    private long collectedCount(StepExecution stepExecution) {
        return stepExecution.getExecutionContext().getLong("collectedCount", 0);
    }

    private long skippedCount(StepExecution stepExecution) {
        return stepExecution.getExecutionContext().getLong("duplicateCount", 0)
                + stepExecution.getReadSkipCount()
                + stepExecution.getProcessSkipCount()
                + stepExecution.getWriteSkipCount();
    }
}
