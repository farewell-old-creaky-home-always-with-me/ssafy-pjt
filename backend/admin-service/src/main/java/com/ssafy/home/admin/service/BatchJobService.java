package com.ssafy.home.admin.service;

import static com.ssafy.home.global.exception.ErrorCode.BATCH_ALREADY_RUNNING;
import static com.ssafy.home.global.exception.ErrorCode.BATCH_INVALID_PARAMETER;
import static com.ssafy.home.global.exception.ErrorCode.BATCH_LAUNCH_FAILED;

import com.ssafy.home.admin.dto.BatchReportGenerateResponse;
import com.ssafy.home.admin.dto.CctvCollectResponse;
import com.ssafy.home.admin.dto.CommercialAreaCollectResponse;
import com.ssafy.home.admin.dto.EnvironmentCollectResponse;
import com.ssafy.home.admin.dto.HouseDealCollectRequest;
import com.ssafy.home.admin.dto.HouseDealCollectResponse;
import com.ssafy.home.admin.dto.HousingNewsCollectResponse;
import com.ssafy.home.admin.dto.RegionCodeCollectResponse;
import com.ssafy.home.batch.domain.HouseType;
import com.ssafy.home.global.exception.CustomException;
import java.time.Clock;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.function.Function;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class BatchJobService {

    public static final String HOUSE_DEAL_JOB_NAME = "houseDealCollectJob";
    public static final String REGION_CODE_JOB_NAME = "regionCodeCollectJob";
    public static final String ALL_REGION_CODE = "ALL";
    public static final String BATCH_REPORT_JOB_NAME = "batchReportGenerateJob";
    public static final String COMMERCIAL_AREA_JOB_NAME = "commercialAreaCollectJob";
    public static final String ENVIRONMENT_JOB_NAME = "environmentCollectJob";
    public static final String HOUSING_NEWS_JOB_NAME = "housingNewsCollectJob";
    public static final String CCTV_JOB_NAME = "cctvCollectJob";
    private static final String REGION_SYNC_SCOPE = "FULL";
    private static final int LAWD_CODE_LENGTH = 5;
    private static final int LEGAL_DONG_CODE_LENGTH = 10;
    private static final DateTimeFormatter YEAR_MONTH = DateTimeFormatter
            .ofPattern("uuuuMM").withResolverStyle(ResolverStyle.STRICT);

    private final JobLauncher jobLauncher;
    private final Job houseDealCollectJob;
    private final Job regionCodeCollectJob;
    private final Job batchReportGenerateJob;
    private final Job commercialAreaCollectJob;
    private final Job environmentCollectJob;
    private final Job housingNewsCollectJob;
    private final Job cctvCollectJob;
    private final Clock clock;

    public BatchJobService(
            @Qualifier("asyncJobLauncher") JobLauncher jobLauncher,
            @Qualifier(HOUSE_DEAL_JOB_NAME) Job houseDealCollectJob,
            @Qualifier(REGION_CODE_JOB_NAME) Job regionCodeCollectJob,
            @Qualifier(BATCH_REPORT_JOB_NAME) Job batchReportGenerateJob,
            @Qualifier(COMMERCIAL_AREA_JOB_NAME) Job commercialAreaCollectJob,
            @Qualifier(ENVIRONMENT_JOB_NAME) Job environmentCollectJob,
            @Qualifier(HOUSING_NEWS_JOB_NAME) Job housingNewsCollectJob,
            @Qualifier(CCTV_JOB_NAME) Job cctvCollectJob,
            Clock clock
    ) {
        this.jobLauncher = jobLauncher;
        this.houseDealCollectJob = houseDealCollectJob;
        this.regionCodeCollectJob = regionCodeCollectJob;
        this.batchReportGenerateJob = batchReportGenerateJob;
        this.commercialAreaCollectJob = commercialAreaCollectJob;
        this.environmentCollectJob = environmentCollectJob;
        this.housingNewsCollectJob = housingNewsCollectJob;
        this.cctvCollectJob = cctvCollectJob;
        this.clock = clock;
    }

    public HouseDealCollectResponse collectHouseDeals(
            Long memberId,
            HouseDealCollectRequest request
    ) {
        validateHouseDealRequest(request);
        String regionCode = normalizeRegionCode(request.regionCode());
        HouseType houseType = HouseType.from(request.houseType());
        JobParameters parameters = new JobParametersBuilder()
                .addString("regionCode", regionCode)
                .addString("yearMonth", request.yearMonth())
                .addString("houseType", houseType.name())
                .addString("dealType", request.dealType())
                .addLong("requestedMemberId", memberId, false)
                .addLong("requestedAt", clock.millis(), false)
                .toJobParameters();
        return launch(houseDealCollectJob, parameters, execution -> new HouseDealCollectResponse(
                execution.getId(),
                HOUSE_DEAL_JOB_NAME,
                execution.getStatus().name(),
                new HouseDealCollectResponse.Parameters(
                        regionCode,
                        request.yearMonth(),
                        houseType.name(),
                        request.dealType()
                )
        ));
    }

    private void validateHouseDealRequest(HouseDealCollectRequest request) {
        try {
            YearMonth.parse(request.yearMonth(), YEAR_MONTH);
        } catch (DateTimeParseException | NullPointerException exception) {
            throw new CustomException(BATCH_INVALID_PARAMETER);
        }
        if (!"SALE".equals(request.dealType())) {
            throw new CustomException(BATCH_INVALID_PARAMETER);
        }
    }

    private String normalizeRegionCode(String regionCode) {
        if (regionCode == null || regionCode.isBlank()) {
            return ALL_REGION_CODE;
        }
        String trimmed = regionCode.trim();
        if (!trimmed.matches("\\d{5}|\\d{10}")) {
            throw new CustomException(BATCH_INVALID_PARAMETER);
        }
        if (trimmed.length() == LEGAL_DONG_CODE_LENGTH) {
            return trimmed.substring(0, LAWD_CODE_LENGTH);
        }
        return trimmed;
    }

    public RegionCodeCollectResponse collectRegionCodes(Long memberId) {
        JobParameters parameters = new JobParametersBuilder()
                .addString("syncScope", REGION_SYNC_SCOPE)
                .addLong("requestedMemberId", memberId, false)
                .addLong("requestedAt", clock.millis(), false)
                .toJobParameters();
        return launch(regionCodeCollectJob, parameters, execution -> new RegionCodeCollectResponse(
                execution.getId(),
                REGION_CODE_JOB_NAME,
                execution.getStatus().name()
        ));
    }

    public BatchReportGenerateResponse generateBatchReport(Long memberId) {
        JobParameters parameters = new JobParametersBuilder()
                .addLong("requestedMemberId", memberId, false)
                .addLong("requestedAt", clock.millis())
                .toJobParameters();
        return launch(batchReportGenerateJob, parameters, execution -> new BatchReportGenerateResponse(
                execution.getId(),
                BATCH_REPORT_JOB_NAME,
                execution.getStatus().name()
        ));
    }

    public CommercialAreaCollectResponse collectCommercialAreas(Long memberId) {
        JobParameters parameters = new JobParametersBuilder()
                .addLong("requestedMemberId", memberId, false)
                .addLong("requestedAt", clock.millis())
                .toJobParameters();
        return launch(commercialAreaCollectJob, parameters, execution -> new CommercialAreaCollectResponse(
                execution.getId(),
                COMMERCIAL_AREA_JOB_NAME,
                execution.getStatus().name()
        ));
    }

    public EnvironmentCollectResponse collectEnvironment(Long memberId) {
        JobParameters parameters = new JobParametersBuilder()
                .addLong("requestedMemberId", memberId, false)
                .addLong("requestedAt", clock.millis())
                .toJobParameters();
        return launch(environmentCollectJob, parameters, execution -> new EnvironmentCollectResponse(
                execution.getId(),
                ENVIRONMENT_JOB_NAME,
                execution.getStatus().name()
        ));
    }

    public HousingNewsCollectResponse collectHousingNews(Long memberId) {
        JobParameters parameters = new JobParametersBuilder()
                .addLong("requestedMemberId", memberId, false)
                .addLong("requestedAt", clock.millis(), false)
                .toJobParameters();
        return launch(housingNewsCollectJob, parameters, execution -> new HousingNewsCollectResponse(
                execution.getId(),
                HOUSING_NEWS_JOB_NAME,
                execution.getStatus().name()
        ));
    }

    public CctvCollectResponse collectCctv(Long memberId) {
        JobParameters parameters = new JobParametersBuilder()
                .addLong("requestedMemberId", memberId, false)
                .addLong("requestedAt", clock.millis(), false)
                .toJobParameters();
        return launch(cctvCollectJob, parameters, execution -> new CctvCollectResponse(
                execution.getId(),
                CCTV_JOB_NAME,
                execution.getStatus().name()
        ));
    }

    private <T> T launch(
            Job job,
            JobParameters parameters,
            Function<JobExecution, T> responseMapper
    ) {
        try {
            JobExecution execution = jobLauncher.run(job, parameters);
            return responseMapper.apply(execution);
        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException exception) {
            throw new CustomException(BATCH_ALREADY_RUNNING);
        } catch (JobRestartException | JobParametersInvalidException exception) {
            throw new CustomException(BATCH_LAUNCH_FAILED);
        }
    }
}
