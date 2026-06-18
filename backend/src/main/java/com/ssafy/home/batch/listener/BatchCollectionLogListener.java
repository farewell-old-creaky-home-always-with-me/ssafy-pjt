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
    private final String dataType;
    private final boolean includeDealParameters;
    private final boolean includeDuplicateSkips;

    private BatchCollectionLogListener(
            BatchCollectionLogMapper mapper,
            String dataType,
            boolean includeDealParameters,
            boolean includeDuplicateSkips
    ) {
        this.mapper = mapper;
        this.dataType = dataType;
        this.includeDealParameters = includeDealParameters;
        this.includeDuplicateSkips = includeDuplicateSkips;
    }

    public static BatchCollectionLogListener forHouseDeal(BatchCollectionLogMapper mapper) {
        return new BatchCollectionLogListener(mapper, "HOUSE_DEAL", true, true);
    }

    public static BatchCollectionLogListener forRegionCode(BatchCollectionLogMapper mapper) {
        return new BatchCollectionLogListener(mapper, "REGION_CODE", false, false);
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
                ? dataType
                : jobExecution.getJobInstance().getJobName();

        mapper.insert(new BatchCollectionLog(
                jobExecution.getId(),
                jobName,
                dataType,
                includeDealParameters ? parameters.getString("regionCode") : null,
                includeDealParameters ? parameters.getString("yearMonth") : null,
                includeDealParameters ? parameters.getString("houseType") : null,
                includeDealParameters ? parameters.getString("dealType") : null,
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
        long duplicateCount = includeDuplicateSkips
                ? stepExecution.getExecutionContext().getLong("duplicateCount", 0)
                : 0;
        return duplicateCount
                + stepExecution.getReadSkipCount()
                + stepExecution.getProcessSkipCount()
                + stepExecution.getWriteSkipCount();
    }
}
