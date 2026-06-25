package com.ssafy.home.batch.listener;

import com.ssafy.home.batch.domain.BatchCollectionLog;
import com.ssafy.home.batch.mapper.BatchCollectionLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;

@Slf4j
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

    public static BatchCollectionLogListener forCommercialArea(BatchCollectionLogMapper mapper) {
        return new BatchCollectionLogListener(mapper, "COMMERCIAL_AREA", false, false);
    }

    public static BatchCollectionLogListener forEnvironment(BatchCollectionLogMapper mapper) {
        return new BatchCollectionLogListener(mapper, "ENVIRONMENT", false, false);
    }

    public static BatchCollectionLogListener forDemographics(BatchCollectionLogMapper mapper) {
        return new BatchCollectionLogListener(mapper, "DEMOGRAPHICS", false, false);
    }

    public static BatchCollectionLogListener forCctv(BatchCollectionLogMapper mapper) {
        return new BatchCollectionLogListener(mapper, "CCTV", false, false);
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

        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            jobExecution.getAllFailureExceptions().forEach(e ->
                    log.error("[BATCH] job={} dataType={} status=FAILED collected={} skipped={} failed={} exception={}",
                            jobName, dataType, collectedCount, skippedCount, failedCount, e.getMessage()));
        }
        logSkipReasons(jobName, jobExecution);
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

    private void logSkipReasons(String jobName, JobExecution jobExecution) {
        jobExecution.getStepExecutions().forEach(stepExecution -> {
            ExecutionContext context = stepExecution.getExecutionContext();
            context.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith("skip.houseDeal.reason."))
                    .forEach(entry -> log.warn(
                            "[BATCH_SKIP_SUMMARY] job={} reason={} count={}",
                            jobName,
                            entry.getKey().substring("skip.houseDeal.reason.".length()),
                            entry.getValue()
                    ));
        });
    }
}
