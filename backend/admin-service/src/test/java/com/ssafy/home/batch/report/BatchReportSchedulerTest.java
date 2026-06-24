package com.ssafy.home.batch.report;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ssafy.home.admin.dto.BatchReportGenerateResponse;
import com.ssafy.home.admin.dto.HouseDealCollectRequest;
import com.ssafy.home.admin.dto.HouseDealCollectResponse;
import com.ssafy.home.admin.service.BatchJobService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

class BatchReportSchedulerTest {

    @Test
    @DisplayName("Scheduler does not launch jobs when disabled")
    void disabledScheduler() {
        BatchJobService batchJobService = Mockito.mock(BatchJobService.class);
        BatchReportScheduler scheduler = new BatchReportScheduler(
                batchJobService,
                new BatchSchedulerProperties(false, "11680", "202606", "APARTMENT", "SALE", 0L),
                Clock.fixed(Instant.parse("2026-06-24T00:00:00Z"), ZoneOffset.UTC),
                Mockito.mock(JobExplorer.class)
        );

        scheduler.collectHouseDeals();
        scheduler.generateBatchReport();

        then(batchJobService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Scheduler launches collection and report jobs when enabled")
    void enabledScheduler() {
        BatchJobService batchJobService = Mockito.mock(BatchJobService.class);
        given(batchJobService.collectHouseDeals(ArgumentMatchers.eq(7L), ArgumentMatchers.any()))
                .willReturn(new HouseDealCollectResponse(10L, "houseDealCollectJob", "STARTED", null));
        given(batchJobService.generateBatchReport(7L))
                .willReturn(new BatchReportGenerateResponse(20L, "batchReportGenerateJob", "STARTED"));
        BatchReportScheduler scheduler = new BatchReportScheduler(
                batchJobService,
                new BatchSchedulerProperties(true, "11680", "", "APARTMENT", "SALE", 7L),
                Clock.fixed(Instant.parse("2026-06-24T00:00:00Z"), ZoneOffset.UTC),
                Mockito.mock(JobExplorer.class)
        );

        scheduler.collectHouseDeals();
        scheduler.generateBatchReport();

        then(batchJobService).should().collectHouseDeals(
                org.mockito.ArgumentMatchers.eq(7L),
                org.mockito.ArgumentMatchers.argThat((HouseDealCollectRequest request) ->
                        request.regionCode().equals("11680")
                                && request.yearMonth().equals("202605")
                                && request.houseType().equals("APARTMENT")
                                && request.dealType().equals("SALE")
                )
        );
        then(batchJobService).should().generateBatchReport(7L);
    }

    @Test
    @DisplayName("Scheduler skips duplicate launch while previous job execution is running")
    void skipWhilePreviousExecutionRunning() {
        BatchJobService batchJobService = Mockito.mock(BatchJobService.class);
        JobExplorer jobExplorer = Mockito.mock(JobExplorer.class);
        given(batchJobService.generateBatchReport(7L))
                .willReturn(new BatchReportGenerateResponse(20L, "batchReportGenerateJob", "STARTED"));
        given(jobExplorer.getJobExecution(20L)).willReturn(jobExecution(20L, BatchStatus.STARTED));
        BatchReportScheduler scheduler = new BatchReportScheduler(
                batchJobService,
                new BatchSchedulerProperties(true, "11680", "", "APARTMENT", "SALE", 7L),
                Clock.fixed(Instant.parse("2026-06-24T00:00:00Z"), ZoneOffset.UTC),
                jobExplorer
        );

        scheduler.generateBatchReport();
        scheduler.generateBatchReport();

        then(batchJobService).should(Mockito.times(1)).generateBatchReport(7L);
    }

    @Test
    @DisplayName("Scheduler skips duplicate house deal collection while previous job execution is running")
    void skipHouseDealCollectionWhilePreviousExecutionRunning() {
        BatchJobService batchJobService = Mockito.mock(BatchJobService.class);
        JobExplorer jobExplorer = Mockito.mock(JobExplorer.class);
        given(batchJobService.collectHouseDeals(ArgumentMatchers.eq(7L), ArgumentMatchers.any()))
                .willReturn(new HouseDealCollectResponse(10L, "houseDealCollectJob", "STARTED", null));
        given(jobExplorer.getJobExecution(10L)).willReturn(jobExecution(10L, BatchStatus.STARTED));
        BatchReportScheduler scheduler = new BatchReportScheduler(
                batchJobService,
                new BatchSchedulerProperties(true, "11680", "", "APARTMENT", "SALE", 7L),
                Clock.fixed(Instant.parse("2026-06-24T00:00:00Z"), ZoneOffset.UTC),
                jobExplorer
        );

        scheduler.collectHouseDeals();
        scheduler.collectHouseDeals();

        then(batchJobService).should(Mockito.times(1))
                .collectHouseDeals(ArgumentMatchers.eq(7L), ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Scheduler launches again after previous job execution is complete")
    void launchAfterPreviousExecutionComplete() {
        BatchJobService batchJobService = Mockito.mock(BatchJobService.class);
        JobExplorer jobExplorer = Mockito.mock(JobExplorer.class);
        given(batchJobService.generateBatchReport(7L))
                .willReturn(
                        new BatchReportGenerateResponse(20L, "batchReportGenerateJob", "STARTED"),
                        new BatchReportGenerateResponse(21L, "batchReportGenerateJob", "STARTED")
                );
        given(jobExplorer.getJobExecution(20L)).willReturn(jobExecution(20L, BatchStatus.COMPLETED));
        BatchReportScheduler scheduler = new BatchReportScheduler(
                batchJobService,
                new BatchSchedulerProperties(true, "11680", "", "APARTMENT", "SALE", 7L),
                Clock.fixed(Instant.parse("2026-06-24T00:00:00Z"), ZoneOffset.UTC),
                jobExplorer
        );

        scheduler.generateBatchReport();
        scheduler.generateBatchReport();

        then(batchJobService).should(Mockito.times(2)).generateBatchReport(7L);
    }

    private JobExecution jobExecution(Long executionId, BatchStatus status) {
        JobExecution execution = new JobExecution(executionId);
        execution.setStatus(status);
        return execution;
    }
}
