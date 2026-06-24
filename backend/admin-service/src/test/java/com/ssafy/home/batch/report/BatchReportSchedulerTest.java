package com.ssafy.home.batch.report;

import static org.mockito.BDDMockito.then;

import com.ssafy.home.admin.dto.HouseDealCollectRequest;
import com.ssafy.home.admin.service.BatchJobService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BatchReportSchedulerTest {

    @Test
    @DisplayName("Scheduler does not launch jobs when disabled")
    void disabledScheduler() {
        BatchJobService batchJobService = Mockito.mock(BatchJobService.class);
        BatchReportScheduler scheduler = new BatchReportScheduler(
                batchJobService,
                new BatchSchedulerProperties(false, "11680", "202606", "APARTMENT", "SALE", 0L),
                Clock.fixed(Instant.parse("2026-06-24T00:00:00Z"), ZoneOffset.UTC)
        );

        scheduler.collectHouseDeals();
        scheduler.generateBatchReport();

        then(batchJobService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Scheduler launches collection and report jobs when enabled")
    void enabledScheduler() {
        BatchJobService batchJobService = Mockito.mock(BatchJobService.class);
        BatchReportScheduler scheduler = new BatchReportScheduler(
                batchJobService,
                new BatchSchedulerProperties(true, "11680", "", "APARTMENT", "SALE", 7L),
                Clock.fixed(Instant.parse("2026-06-24T00:00:00Z"), ZoneOffset.UTC)
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
}
