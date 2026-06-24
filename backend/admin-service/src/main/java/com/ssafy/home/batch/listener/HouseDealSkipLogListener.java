package com.ssafy.home.batch.listener;

import com.ssafy.home.batch.domain.NormalizedHouseDeal;
import com.ssafy.home.batch.processor.InvalidHouseDealException;
import com.ssafy.home.external.molit.MolitRawHouseDeal;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;

@Slf4j
public class HouseDealSkipLogListener implements
        SkipListener<MolitRawHouseDeal, NormalizedHouseDeal>,
        StepExecutionListener {

    private static final String SKIP_REASON_PREFIX = "skip.houseDeal.reason.";
    private static final int SAMPLE_LOG_LIMIT = 20;
    private static final int SAMPLE_LOG_INTERVAL = 1000;

    private StepExecution stepExecution;
    private final Map<String, Long> counts = new HashMap<>();

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        counts.forEach((key, value) -> stepExecution.getExecutionContext().putLong(key, value));
        return null;
    }

    @Override
    public void onSkipInProcess(MolitRawHouseDeal item, Throwable throwable) {
        if (!(throwable instanceof InvalidHouseDealException exception)) {
            return;
        }
        long count = increment(SKIP_REASON_PREFIX + exception.reason());
        if (count <= SAMPLE_LOG_LIMIT || count % SAMPLE_LOG_INTERVAL == 0) {
            log.warn(
                    "[BATCH_SKIP] reason={} count={} message={} lawdCode={} dongName={} name={} jibun={}",
                    exception.reason(),
                    count,
                    exception.getMessage(),
                    item == null ? null : item.lawdCode(),
                    item == null ? null : item.legalDongName(),
                    item == null ? null : item.name(),
                    item == null ? null : item.jibun()
            );
        }
    }

    private long increment(String key) {
        long count = counts.getOrDefault(key, 0L) + 1;
        counts.put(key, count);
        if (stepExecution == null) {
            return count;
        }
        ExecutionContext context = stepExecution.getExecutionContext();
        context.putLong(key, count);
        return count;
    }
}
