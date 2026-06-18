package com.ssafy.home.admin.service;

import static com.ssafy.home.global.exception.ErrorCode.BATCH_ALREADY_RUNNING;
import static com.ssafy.home.global.exception.ErrorCode.BATCH_INVALID_PARAMETER;
import static com.ssafy.home.global.exception.ErrorCode.BATCH_LAUNCH_FAILED;

import com.ssafy.home.admin.dto.HouseDealCollectRequest;
import com.ssafy.home.admin.dto.HouseDealCollectResponse;
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
import org.springframework.transaction.annotation.Transactional;

@Service
public class BatchJobService {

    public static final String HOUSE_DEAL_JOB_NAME = "houseDealCollectJob";
    public static final String REGION_CODE_JOB_NAME = "regionCodeCollectJob";
    private static final String REGION_SYNC_SCOPE = "FULL";
    private static final DateTimeFormatter YEAR_MONTH = DateTimeFormatter
            .ofPattern("uuuuMM").withResolverStyle(ResolverStyle.STRICT);

    private final JobLauncher jobLauncher;
    private final Job houseDealCollectJob;
    private final Job regionCodeCollectJob;
    private final Clock clock;

    public BatchJobService(
            @Qualifier("asyncJobLauncher") JobLauncher jobLauncher,
            @Qualifier(HOUSE_DEAL_JOB_NAME) Job houseDealCollectJob,
            @Qualifier(REGION_CODE_JOB_NAME) Job regionCodeCollectJob,
            Clock clock
    ) {
        this.jobLauncher = jobLauncher;
        this.houseDealCollectJob = houseDealCollectJob;
        this.regionCodeCollectJob = regionCodeCollectJob;
        this.clock = clock;
    }

    @Transactional
    public HouseDealCollectResponse collectHouseDeals(
            Long memberId,
            HouseDealCollectRequest request
    ) {
        validateHouseDealRequest(request);
        HouseType houseType = HouseType.from(request.houseType());
        JobParameters parameters = new JobParametersBuilder()
                .addString("regionCode", request.regionCode())
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
                        request.regionCode(),
                        request.yearMonth(),
                        houseType.name(),
                        request.dealType()
                )
        ));
    }

    @Transactional
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
}
