package com.ssafy.home.batch.writer;

import com.ssafy.home.batch.domain.NormalizedHouseDeal;
import com.ssafy.home.batch.mapper.HouseDealBatchMapper;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.DuplicateKeyException;

public class HouseDealWriter implements ItemWriter<NormalizedHouseDeal> {

    private final HouseDealBatchMapper mapper;
    private StepExecution stepExecution;

    public HouseDealWriter(HouseDealBatchMapper mapper) {
        this.mapper = mapper;
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public void write(Chunk<? extends NormalizedHouseDeal> chunk) {
        long collected = context().getLong("collectedCount", 0);
        long duplicates = context().getLong("duplicateCount", 0);
        for (NormalizedHouseDeal deal : chunk) {
            long houseId = resolveHouse(deal);
            if (insertDeal(houseId, deal)) {
                collected++;
            } else {
                duplicates++;
            }
        }
        context().putLong("collectedCount", collected);
        context().putLong("duplicateCount", duplicates);
    }

    private long resolveHouse(NormalizedHouseDeal deal) {
        Long id = mapper.findHouseId(deal);
        if (id != null) {
            return id;
        }
        try {
            mapper.insertHouse(deal);
        } catch (DuplicateKeyException exception) {
            id = mapper.findHouseId(deal);
            if (id == null) {
                throw exception;
            }
            return id;
        }
        id = mapper.findHouseId(deal);
        if (id == null) {
            throw new IllegalStateException("Inserted house not found");
        }
        return id;
    }

    private boolean insertDeal(long houseId, NormalizedHouseDeal deal) {
        try {
            return mapper.insertDealIfAbsent(houseId, deal) == 1;
        } catch (DuplicateKeyException exception) {
            if (mapper.existsDeal(houseId, deal)) {
                return false;
            }
            throw exception;
        }
    }

    private ExecutionContext context() {
        if (stepExecution == null) {
            throw new IllegalStateException("StepExecution is not initialized");
        }
        return stepExecution.getExecutionContext();
    }
}
