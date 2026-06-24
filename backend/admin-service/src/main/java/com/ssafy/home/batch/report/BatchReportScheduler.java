package com.ssafy.home.batch.report;

import com.ssafy.home.admin.dto.BatchReportGenerateResponse;
import com.ssafy.home.admin.dto.HouseDealCollectRequest;
import com.ssafy.home.admin.dto.HouseDealCollectResponse;
import com.ssafy.home.admin.service.BatchJobService;
import java.time.Clock;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BatchReportScheduler {

    private static final DateTimeFormatter YEAR_MONTH = DateTimeFormatter.ofPattern("yyyyMM");

    private final BatchJobService batchJobService;
    private final BatchSchedulerProperties properties;
    private final Clock clock;
    private final JobExplorer jobExplorer;
    private final AtomicBoolean houseDealRunning = new AtomicBoolean(false);
    private final AtomicReference<Long> houseDealExecutionId = new AtomicReference<>();
    private final AtomicBoolean reportRunning = new AtomicBoolean(false);
    private final AtomicReference<Long> reportExecutionId = new AtomicReference<>();

    public BatchReportScheduler(
            BatchJobService batchJobService,
            BatchSchedulerProperties properties,
            Clock clock,
            JobExplorer jobExplorer
    ) {
        this.batchJobService = batchJobService;
        this.properties = properties;
        this.clock = clock;
        this.jobExplorer = jobExplorer;
    }

    @Scheduled(cron = "${batch.scheduler.house-deal-cron:0 0 3 * * *}")
    public void collectHouseDeals() {
        if (!properties.enabled()) {
            return;
        }
        if (!tryStart(houseDealRunning, houseDealExecutionId)) {
            log.info("Skip scheduled house deal collection because previous execution is still running");
            return;
        }
        try {
            HouseDealCollectResponse response = batchJobService.collectHouseDeals(
                    properties.systemMemberId(),
                    new HouseDealCollectRequest(
                            properties.regionCode(),
                            resolveYearMonth(),
                            properties.houseType(),
                            properties.dealType()
                    )
            );
            houseDealExecutionId.set(response.jobExecutionId());
        } catch (RuntimeException exception) {
            houseDealRunning.set(false);
            houseDealExecutionId.set(null);
            throw exception;
        }
    }

    @Scheduled(cron = "${batch.scheduler.report-cron:0 30 3 * * *}")
    public void generateBatchReport() {
        if (!properties.enabled()) {
            return;
        }
        if (!tryStart(reportRunning, reportExecutionId)) {
            log.info("Skip scheduled batch report generation because previous execution is still running");
            return;
        }
        try {
            BatchReportGenerateResponse response = batchJobService.generateBatchReport(properties.systemMemberId());
            reportExecutionId.set(response.jobExecutionId());
        } catch (RuntimeException exception) {
            reportRunning.set(false);
            reportExecutionId.set(null);
            throw exception;
        }
    }

    private boolean tryStart(AtomicBoolean running, AtomicReference<Long> executionId) {
        if (running.compareAndSet(false, true)) {
            return true;
        }
        if (isExecutionRunning(executionId.get())) {
            return false;
        }
        running.set(false);
        executionId.set(null);
        return running.compareAndSet(false, true);
    }

    private boolean isExecutionRunning(Long executionId) {
        if (executionId == null) {
            return false;
        }
        JobExecution execution = jobExplorer.getJobExecution(executionId);
        return execution != null && execution.isRunning();
    }

    private String resolveYearMonth() {
        if (properties.yearMonth() != null && !properties.yearMonth().isBlank()) {
            return properties.yearMonth();
        }
        return YearMonth.now(clock).minusMonths(1).format(YEAR_MONTH);
    }
}
