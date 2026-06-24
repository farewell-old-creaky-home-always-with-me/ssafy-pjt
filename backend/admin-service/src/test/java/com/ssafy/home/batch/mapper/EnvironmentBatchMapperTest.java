package com.ssafy.home.batch.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.batch.domain.NormalizedEnvironment;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@MybatisTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class EnvironmentBatchMapperTest {

    @Autowired
    private EnvironmentBatchMapper mapper;

    @Test
    @DisplayName("새 환경 정보를 INSERT한다")
    void upsertInsertsNewEnvironment() {
        NormalizedEnvironment environment = environment("대기 배출 - 테스트 배출시설", "12.3400");

        int affected = mapper.upsert(environment);

        assertThat(affected).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("같은 항목명, 측정일, 좌표로 재INSERT하면 UPDATE된다")
    void upsertUpdatesExistingEnvironment() {
        NormalizedEnvironment original = environment("대기 배출 - 테스트 배출시설", "12.3400");
        NormalizedEnvironment updated = environment("대기 배출 - 테스트 배출시설", "56.7800");
        mapper.upsert(original);

        int affected = mapper.upsert(updated);

        assertThat(affected).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("측정일이 없어도 같은 항목명과 좌표로 재INSERT하면 UPDATE된다")
    void upsertUpdatesExistingEnvironmentWithoutMeasuredDate() {
        NormalizedEnvironment original = environment("녹지 - 테스트 녹지", "1.0000", null);
        NormalizedEnvironment updated = environment("녹지 - 테스트 녹지", "2.0000", null);
        mapper.upsert(original);

        int affected = mapper.upsert(updated);

        assertThat(affected).isGreaterThanOrEqualTo(1);
    }

    private NormalizedEnvironment environment(String itemName, String value) {
        return environment(itemName, value, LocalDate.of(2026, 5, 30));
    }

    private NormalizedEnvironment environment(String itemName, String value, LocalDate measuredDate) {
        return new NormalizedEnvironment(
                itemName,
                new BigDecimal(value),
                "kg",
                measuredDate,
                new BigDecimal("37.5665000"),
                new BigDecimal("126.9780000")
        );
    }
}
