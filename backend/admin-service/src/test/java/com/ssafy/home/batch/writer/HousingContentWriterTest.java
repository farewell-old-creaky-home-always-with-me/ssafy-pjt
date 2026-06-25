package com.ssafy.home.batch.writer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.ssafy.home.batch.domain.HousingInfoType;
import com.ssafy.home.batch.domain.HousingNewsCategory;
import com.ssafy.home.batch.domain.NormalizedHousingInfo;
import com.ssafy.home.batch.domain.NormalizedHousingNews;
import com.ssafy.home.batch.mapper.HousingNewsBatchMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.Chunk;

class HousingContentWriterTest {

    private HousingNewsBatchMapper mapper;
    private HousingContentWriter writer;

    @BeforeEach
    void setUp() {
        mapper = mock(HousingNewsBatchMapper.class);
        writer = new HousingContentWriter(mapper);
    }

    @Test
    @DisplayName("뉴스와 정보를 각 테이블 upsert로 라우팅하고 수집 건수를 증가시킨다")
    void writeRoutesItemsAndIncrementsCollectedCount() throws Exception {
        // Given
        StepExecution stepExecution = new StepExecution("housingNewsCollectStep", new JobExecution(1L));
        stepExecution.getExecutionContext().putLong("collectedCount", 5);
        writer.beforeStep(stepExecution);
        NormalizedHousingNews news = news();
        NormalizedHousingInfo info = info();

        // When
        writer.write(Chunk.of(news, info));

        // Then
        verify(mapper).upsertNews(news);
        verify(mapper).upsertInfo(info);
        assertThat(stepExecution.getExecutionContext().getLong("collectedCount")).isEqualTo(7);
    }

    @Test
    @DisplayName("StepExecution 초기화 전에 쓰면 예외를 던진다")
    void writeRequiresBeforeStep() {
        // Given
        NormalizedHousingNews news = news();

        // When / Then
        assertThatThrownBy(() -> writer.write(Chunk.of(news)))
                .isInstanceOf(IllegalStateException.class);
    }

    private NormalizedHousingNews news() {
        return new NormalizedHousingNews(
                "시장 동향",
                "요약",
                "https://news.example.com/1",
                "국토부",
                HousingNewsCategory.MARKET,
                LocalDateTime.of(2026, 6, 24, 9, 0)
        );
    }

    private NormalizedHousingInfo info() {
        return new NormalizedHousingInfo(
                "청약 안내",
                "본문",
                "https://info.example.com/1",
                "LH",
                HousingInfoType.POLICY,
                LocalDateTime.of(2026, 6, 24, 10, 0)
        );
    }
}
