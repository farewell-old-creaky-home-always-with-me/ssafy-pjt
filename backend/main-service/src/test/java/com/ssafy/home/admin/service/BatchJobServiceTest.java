package com.ssafy.home.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

import static com.ssafy.home.global.exception.ErrorCode.BATCH_ALREADY_RUNNING;

import com.ssafy.home.admin.dto.RegionCodeCollectResponse;
import com.ssafy.home.global.exception.CustomException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;

@ExtendWith(MockitoExtension.class)
class BatchJobServiceTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job houseDealCollectJob;

    @Mock
    private Job regionCodeCollectJob;

    private BatchJobService batchJobService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-06-17T00:00:00Z"), ZoneId.of("Asia/Seoul"));
        batchJobService = new BatchJobService(
                jobLauncher,
                houseDealCollectJob,
                regionCodeCollectJob,
                clock
        );
    }

    @Test
    @DisplayName("법정동 수집 Job을 실행한다")
    void collectRegionCodesLaunchesJob() throws Exception {
        // given
        JobExecution execution = new JobExecution(10L);
        given(jobLauncher.run(org.mockito.ArgumentMatchers.eq(regionCodeCollectJob),
                org.mockito.ArgumentMatchers.any(JobParameters.class)))
                .willReturn(execution);

        // when
        RegionCodeCollectResponse response = batchJobService.collectRegionCodes(1L);

        // then
        assertThat(response.jobExecutionId()).isEqualTo(10L);
        assertThat(response.jobName()).isEqualTo(BatchJobService.REGION_CODE_JOB_NAME);
    }

    @Test
    @DisplayName("실행 중인 법정동 수집 Job이 있으면 예외를 던진다")
    void collectRegionCodesThrowsWhenAlreadyRunning() throws Exception {
        // given
        willThrow(new JobExecutionAlreadyRunningException("running"))
                .given(jobLauncher)
                .run(org.mockito.ArgumentMatchers.eq(regionCodeCollectJob),
                        org.mockito.ArgumentMatchers.any(JobParameters.class));

        // when / then
        assertThatThrownBy(() -> batchJobService.collectRegionCodes(1L))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(BATCH_ALREADY_RUNNING));
    }
}
