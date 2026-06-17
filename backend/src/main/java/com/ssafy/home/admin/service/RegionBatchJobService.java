package com.ssafy.home.admin.service;

import com.ssafy.home.admin.dto.RegionCodeCollectResponse;
import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.exception.ErrorCode;
import java.time.Clock;
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
public class RegionBatchJobService {

    public static final String JOB_NAME = "regionCodeCollectJob";
    private static final String SYNC_SCOPE = "FULL";

    private final JobLauncher jobLauncher;
    private final Job job;
    private final Clock clock;

    public RegionBatchJobService(
            @Qualifier("asyncJobLauncher") JobLauncher jobLauncher,
            @Qualifier(JOB_NAME) Job job,
            Clock clock
    ) {
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.clock = clock;
    }

    public RegionCodeCollectResponse collectRegionCodes(Long memberId) {
        JobParameters parameters = new JobParametersBuilder()
                .addString("syncScope", SYNC_SCOPE)
                .addLong("requestedMemberId", memberId, false)
                .addLong("requestedAt", clock.millis())
                .toJobParameters();
        try {
            JobExecution execution = jobLauncher.run(job, parameters);
            return new RegionCodeCollectResponse(
                    execution.getId(),
                    JOB_NAME,
                    execution.getStatus().name()
            );
        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException exception) {
            throw new CustomException(ErrorCode.BATCH_ALREADY_RUNNING);
        } catch (JobRestartException | JobParametersInvalidException exception) {
            throw new CustomException(ErrorCode.BATCH_LAUNCH_FAILED);
        }
    }
}
