package com.ssafy.home.batch.report;

import com.ssafy.home.admin.dto.HouseDealCollectRequest;
import com.ssafy.home.admin.service.BatchJobService;
import java.time.Clock;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BatchReportScheduler {

    private static final DateTimeFormatter YEAR_MONTH = DateTimeFormatter.ofPattern("yyyyMM");

    private final BatchJobService batchJobService;
    private final BatchSchedulerProperties properties;
    private final Clock clock;
    private final AtomicBoolean houseDealRunning = new AtomicBoolean(false);
    private final AtomicBoolean reportRunning = new AtomicBoolean(false);

    public BatchReportScheduler(BatchJobService batchJobService, BatchSchedulerProperties properties, Clock clock) {
        this.batchJobService = batchJobService;
        this.properties = properties;
        this.clock = clock;
    }

    @Scheduled(cron = "${batch.scheduler.house-deal-cron:0 0 3 * * *}")
    public void collectHouseDeals() {
        if (!properties.enabled()) {
            return;
        }
        if (!houseDealRunning.compareAndSet(false, true)) {
            log.info("Skip scheduled house deal collection because previous execution is still running");
            return;
        }
        try {
            batchJobService.collectHouseDeals(
                    properties.systemMemberId(),
                    new HouseDealCollectRequest(
                            properties.regionCode(),
                            resolveYearMonth(),
                            properties.houseType(),
                            properties.dealType()
                    )
            );
        } finally {
            houseDealRunning.set(false);
        }
    }

    @Scheduled(cron = "${batch.scheduler.report-cron:0 30 3 * * *}")
    public void generateBatchReport() {
        if (!properties.enabled()) {
            return;
        }
        if (!reportRunning.compareAndSet(false, true)) {
            log.info("Skip scheduled batch report generation because previous execution is still running");
            return;
        }
        try {
            batchJobService.generateBatchReport(properties.systemMemberId());
        } finally {
            reportRunning.set(false);
        }
    }

    private String resolveYearMonth() {
        if (properties.yearMonth() != null && !properties.yearMonth().isBlank()) {
            return properties.yearMonth();
        }
        return YearMonth.now(clock).minusMonths(1).format(YEAR_MONTH);
    }
}
