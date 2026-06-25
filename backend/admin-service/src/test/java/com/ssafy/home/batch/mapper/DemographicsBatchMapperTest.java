package com.ssafy.home.batch.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.batch.domain.NormalizedPopulation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@MybatisTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class DemographicsBatchMapperTest {

    @Autowired
    private DemographicsBatchMapper mapper;

    @Test
    @DisplayName("인구통계를 INSERT한다")
    void upsertPopulationInsertsNewRow() {
        // When
        int affected = mapper.upsertPopulation(population("202505"));

        // Then
        assertThat(affected).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("같은 동·기준연월로 재INSERT하면 UPDATE된다")
    void upsertPopulationUpdatesExistingRow() {
        // Given
        mapper.upsertPopulation(population("202505"));
        NormalizedPopulation updated = new NormalizedPopulation(
                "서울특별시", "강남구", "역삼1동", 99999, 9999, 999, "202505");

        // When
        int affected = mapper.upsertPopulation(updated);

        // Then
        assertThat(affected).isGreaterThanOrEqualTo(1);
    }

    private NormalizedPopulation population(String referenceDate) {
        return new NormalizedPopulation(
                "서울특별시", "강남구", "역삼1동", 12345, 5678, 1234, referenceDate);
    }
}
