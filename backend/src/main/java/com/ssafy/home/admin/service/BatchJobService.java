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

    public static final String JOB_NAME = "houseDealCollectJob";
    private static final DateTimeFormatter YEAR_MONTH = DateTimeFormatter
            .ofPattern("uuuuMM").withResolverStyle(ResolverStyle.STRICT);

    private final JobLauncher jobLauncher;
    private final Job job;
    private final Clock clock;

    public BatchJobService(
            @Qualifier("asyncJobLauncher") JobLauncher jobLauncher,
            @Qualifier(JOB_NAME) Job job,
            Clock clock
    ) {
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.clock = clock;
    }

    public HouseDealCollectResponse collectHouseDeals(
            Long memberId,
            HouseDealCollectRequest request
    ) {
        validate(request);
        HouseType houseType = HouseType.from(request.houseType());
        // identifying 파라미터(regionCode/yearMonth/houseType/dealType)가 JobInstance 키를
        // 결정하므로, BATCH_JOB_INSTANCE 유니크 제약이 중복 실행을 원자적으로 차단한다.
        JobParameters parameters = new JobParametersBuilder()
                .addString("regionCode", request.regionCode())
                .addString("yearMonth", request.yearMonth())
                .addString("houseType", houseType.name())
                .addString("dealType", request.dealType())
                .addLong("requestedMemberId", memberId, false)
                .addLong("requestedAt", clock.millis(), false)
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
        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException exception) {
            throw new CustomException(ErrorCode.BATCH_ALREADY_RUNNING);
        } catch (JobRestartException | JobParametersInvalidException exception) {
            throw new CustomException(ErrorCode.BATCH_LAUNCH_FAILED);
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
}
