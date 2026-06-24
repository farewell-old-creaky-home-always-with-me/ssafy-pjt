package com.ssafy.home.batch.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@MybatisTest
@Sql("/sql/house-deal-batch-data.sql")
class HouseDealBatchMapperTest {

    @Autowired
    private HouseDealBatchMapper houseDealBatchMapper;

    @Test
    @DisplayName("법정동 코드에서 공공데이터 수집용 5자리 코드를 중복 없이 조회한다")
    void findAllLawdCodes() {
        // When
        List<String> lawdCodes = houseDealBatchMapper.findAllLawdCodes();

        // Then
        assertThat(lawdCodes).containsExactly("11110", "11140");
    }
}
