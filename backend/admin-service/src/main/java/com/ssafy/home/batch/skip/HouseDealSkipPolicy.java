package com.ssafy.home.batch.skip;

import com.ssafy.home.batch.processor.InvalidHouseDealException;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

public class HouseDealSkipPolicy implements SkipPolicy {

    private static final String UNKNOWN_REGION_CODE = "UNKNOWN_REGION_CODE";

    private final int invalidDataSkipLimit;
    private long invalidDataSkipCount;

    public HouseDealSkipPolicy(int invalidDataSkipLimit) {
        this.invalidDataSkipLimit = invalidDataSkipLimit;
    }

    @Override
    public boolean shouldSkip(Throwable throwable, long skipCount) throws SkipLimitExceededException {
        if (!(throwable instanceof InvalidHouseDealException exception)) {
            return false;
        }
        if (UNKNOWN_REGION_CODE.equals(exception.reason())) {
            return true;
        }
        if (invalidDataSkipCount >= invalidDataSkipLimit) {
            throw new SkipLimitExceededException(invalidDataSkipLimit, throwable);
        }
        invalidDataSkipCount++;
        return true;
    }
}
