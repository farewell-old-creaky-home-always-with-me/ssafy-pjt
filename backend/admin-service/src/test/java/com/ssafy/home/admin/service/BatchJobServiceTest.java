package com.ssafy.home.admin.service;

import static com.ssafy.home.global.exception.ErrorCode.BATCH_INVALID_PARAMETER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.ssafy.home.admin.dto.EnvironmentCollectResponse;
import com.ssafy.home.admin.dto.HouseDealCollectRequest;
import com.ssafy.home.admin.dto.HouseDealCollectResponse;
import com.ssafy.home.admin.dto.HousingNewsCollectResponse;
import com.ssafy.home.global.exception.CustomException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

@ExtendWith(MockitoExtension.class)
class BatchJobServiceTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job houseDealCollectJob;

    @Mock
    private Job regionCodeCollectJob;

    @Mock
    private Job batchReportGenerateJob;

    @Mock
    private Job commercialAreaCollectJob;

    @Mock
    private Job environmentCollectJob;

    @Mock
    private Job demographicsCollectJob;

    @Mock
    private Job housingNewsCollectJob;

    @Mock
    private Job cctvCollectJob;

    private BatchJobService service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-06-24T00:00:00Z"), ZoneId.of("UTC"));
        service = new BatchJobService(
                jobLauncher,
                houseDealCollectJob,
                regionCodeCollectJob,
                batchReportGenerateJob,
                commercialAreaCollectJob,
                environmentCollectJob,
                demographicsCollectJob,
                housingNewsCollectJob,
                cctvCollectJob,
                clock
        );
    }

    @Test
    @DisplayName("행정구역 코드가 비어 있으면 전체 지역 수집으로 실행한다")
    void collectHouseDealsWithBlankRegionCodeRunsAllRegions() throws Exception {
        // Given
        HouseDealCollectRequest request = new HouseDealCollectRequest(
                "",
                "202605",
                "APARTMENT",
                "SALE"
        );
        ArgumentCaptor<JobParameters> parametersCaptor = ArgumentCaptor.forClass(JobParameters.class);
        given(jobLauncher.run(eq(houseDealCollectJob), parametersCaptor.capture()))
                .willReturn(new JobExecution(1L));

        // When
        HouseDealCollectResponse response = service.collectHouseDeals(1L, request);

        // Then
        assertThat(parametersCaptor.getValue().getString("regionCode")).isEqualTo("ALL");
        assertThat(response.parameters().regionCode()).isEqualTo("ALL");
    }

    @Test
    @DisplayName("10자리 법정동 코드는 공공데이터 수집용 5자리 코드로 변환한다")
    void collectHouseDealsWithDongCodeUsesLawdCode() throws Exception {
        // Given
        HouseDealCollectRequest request = new HouseDealCollectRequest(
                "1111010100",
                "202605",
                "APARTMENT",
                "SALE"
        );
        ArgumentCaptor<JobParameters> parametersCaptor = ArgumentCaptor.forClass(JobParameters.class);
        given(jobLauncher.run(eq(houseDealCollectJob), parametersCaptor.capture()))
                .willReturn(new JobExecution(1L));

        // When
        HouseDealCollectResponse response = service.collectHouseDeals(1L, request);

        // Then
        assertThat(parametersCaptor.getValue().getString("regionCode")).isEqualTo("11110");
        assertThat(response.parameters().regionCode()).isEqualTo("11110");
    }

    @Test
    @DisplayName("행정구역 코드는 비어 있거나 5자리 또는 10자리 숫자여야 한다")
    void collectHouseDealsRejectsInvalidRegionCode() {
        // Given
        HouseDealCollectRequest request = new HouseDealCollectRequest(
                "111101",
                "202605",
                "APARTMENT",
                "SALE"
        );

        // When / Then
        assertThatThrownBy(() -> service.collectHouseDeals(1L, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(BATCH_INVALID_PARAMETER);
    }

    @Test
    @DisplayName("환경 정보 수집 배치를 실행한다")
    void collectEnvironmentRunsEnvironmentJob() throws Exception {
        // Given
        ArgumentCaptor<JobParameters> parametersCaptor = ArgumentCaptor.forClass(JobParameters.class);
        given(jobLauncher.run(eq(environmentCollectJob), parametersCaptor.capture()))
                .willReturn(new JobExecution(9L));

        // When
        EnvironmentCollectResponse response = service.collectEnvironment(1L);

        // Then
        assertThat(parametersCaptor.getValue().getLong("requestedMemberId")).isEqualTo(1L);
        assertThat(response.executionId()).isEqualTo(9L);
        assertThat(response.jobName()).isEqualTo("environmentCollectJob");
        assertThat(response.status()).isEqualTo("STARTING");
    }

    @Test
    @DisplayName("주거 뉴스 수집 배치를 실행한다")
    void collectHousingNewsRunsHousingNewsJob() throws Exception {
        // Given
        ArgumentCaptor<JobParameters> parametersCaptor = ArgumentCaptor.forClass(JobParameters.class);
        given(jobLauncher.run(eq(housingNewsCollectJob), parametersCaptor.capture()))
                .willReturn(new JobExecution(10L));

        // When
        HousingNewsCollectResponse response = service.collectHousingNews(7L);

        // Then
        assertThat(parametersCaptor.getValue().getLong("requestedMemberId")).isEqualTo(7L);
        assertThat(parametersCaptor.getValue().getLong("requestedAt")).isEqualTo(1_782_259_200_000L);
        assertThat(response.executionId()).isEqualTo(10L);
        assertThat(response.jobName()).isEqualTo("housingNewsCollectJob");
        assertThat(response.status()).isEqualTo("STARTING");
    }
}
