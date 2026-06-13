package com.ssafy.home.admin.service;

import com.ssafy.home.admin.dto.HouseDealCollectRequest;
import com.ssafy.home.admin.dto.HouseDealCollectResponse;
import com.ssafy.home.batch.domain.HouseType;
import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.exception.ErrorCode;
import java.time.Clock;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Objects;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class BatchJobService {

    public static final String JOB_NAME = "houseDealCollectJob";
    private static final DateTimeFormatter YEAR_MONTH = DateTimeFormatter
            .ofPattern("uuuuMM").withResolverStyle(ResolverStyle.STRICT);

    private final JobLauncher jobLauncher;
    private final Job job;
    private final JobExplorer jobExplorer;
    private final Clock clock;
    private final Object launchLock = new Object();

    public BatchJobService(
            @Qualifier("asyncJobLauncher") JobLauncher jobLauncher,
            @Qualifier(JOB_NAME) Job job,
            JobExplorer jobExplorer,
            Clock clock
    ) {
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.jobExplorer = jobExplorer;
        this.clock = clock;
    }

    public HouseDealCollectResponse collectHouseDeals(
            Long memberId,
            HouseDealCollectRequest request
    ) {
        validate(request);
        HouseType houseType = HouseType.from(request.houseType());
        synchronized (launchLock) {
            rejectMatchingExecution(request, houseType);
            JobParameters parameters = new JobParametersBuilder()
                    .addString("regionCode", request.regionCode())
                    .addString("yearMonth", request.yearMonth())
                    .addString("houseType", houseType.name())
                    .addString("dealType", request.dealType())
                    .addLong("requestedMemberId", memberId)
                    .addLong("requestedAt", clock.millis())
                    .toJobParameters();
            try {
                JobExecution execution = jobLauncher.run(job, parameters);
                return new HouseDealCollectResponse(
                        execution.getId(), JOB_NAME, execution.getStatus().name(),
                        new HouseDealCollectResponse.Parameters(
                                request.regionCode(), request.yearMonth(),
                                houseType.name(), request.dealType()
                        )
                );
            } catch (JobExecutionAlreadyRunningException exception) {
                throw new CustomException(ErrorCode.BATCH_ALREADY_RUNNING);
            } catch (JobRestartException | JobInstanceAlreadyCompleteException
                     | JobParametersInvalidException exception) {
                throw new CustomException(ErrorCode.BATCH_LAUNCH_FAILED);
            }
        }
    }

    private void validate(HouseDealCollectRequest request) {
        try {
            YearMonth.parse(request.yearMonth(), YEAR_MONTH);
        } catch (DateTimeParseException | NullPointerException exception) {
            throw new CustomException(ErrorCode.BATCH_INVALID_PARAMETER);
        }
        if (!"SALE".equals(request.dealType())) {
            throw new CustomException(ErrorCode.BATCH_INVALID_PARAMETER);
        }
    }

    private void rejectMatchingExecution(HouseDealCollectRequest request, HouseType houseType) {
        boolean running = jobExplorer.findRunningJobExecutions(JOB_NAME).stream()
                .map(JobExecution::getJobParameters)
                .anyMatch(parameters -> matches(parameters, request, houseType));
        if (running) {
            throw new CustomException(ErrorCode.BATCH_ALREADY_RUNNING);
        }
    }

    private boolean matches(
            JobParameters parameters,
            HouseDealCollectRequest request,
            HouseType houseType
    ) {
        return Objects.equals(parameters.getString("regionCode"), request.regionCode())
                && Objects.equals(parameters.getString("yearMonth"), request.yearMonth())
                && Objects.equals(parameters.getString("houseType"), houseType.name())
                && Objects.equals(parameters.getString("dealType"), request.dealType());
    }
}
