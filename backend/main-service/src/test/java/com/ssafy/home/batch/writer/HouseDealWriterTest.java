package com.ssafy.home.batch.writer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.ssafy.home.batch.domain.NormalizedHouseDeal;
import com.ssafy.home.batch.mapper.HouseDealBatchMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.Chunk;

@ExtendWith(MockitoExtension.class)
class HouseDealWriterTest {

    @Mock
    private HouseDealBatchMapper mapper;

    @Test
    void countsInsertedAndDuplicateDeals() throws Exception {
        NormalizedHouseDeal inserted = deal("신규 아파트");
        NormalizedHouseDeal duplicate = deal("중복 아파트");
        when(mapper.findHouseId(inserted)).thenReturn(1L);
        when(mapper.findHouseId(duplicate)).thenReturn(2L);
        when(mapper.insertDealIfAbsent(1L, inserted)).thenReturn(1);
        when(mapper.insertDealIfAbsent(2L, duplicate)).thenReturn(0);
        HouseDealWriter writer = new HouseDealWriter(mapper);
        StepExecution stepExecution = new StepExecution(
                "houseDealCollectStep", new JobExecution(1L)
        );
        writer.beforeStep(stepExecution);

        writer.write(new Chunk<>(List.of(inserted, duplicate)));

        assertThat(stepExecution.getExecutionContext().getLong("collectedCount")).isEqualTo(1);
        assertThat(stepExecution.getExecutionContext().getLong("duplicateCount")).isEqualTo(1);
    }

    private NormalizedHouseDeal deal(String name) {
        return new NormalizedHouseDeal(
                "1111010100", name, "12-3", 2001, "아파트",
                "매매", 123456, null, 0, LocalDate.of(2026, 5, 7),
                new BigDecimal("84.95"), 10
        );
    }
}
