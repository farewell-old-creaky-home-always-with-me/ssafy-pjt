package com.ssafy.home.batch.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.batch.domain.NormalizedHouseDeal;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@MybatisTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:house-deal-mapper;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.sql.init.mode=never",
        "mybatis.mapper-locations=classpath:/mapper/*.xml"
})
@Sql(scripts = "/schema-test.sql")
class HouseDealBatchMapperTest {

    @Autowired
    private HouseDealBatchMapper mapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("""
                INSERT INTO region_code(region_code, sido_name, sigungu_name, dong_name)
                VALUES (?, ?, ?, ?)
                """, "1111010100", "서울특별시", "종로구", "청운동");
    }

    @Test
    void insertsHouseOnceAndIgnoresDuplicateDeal() {
        NormalizedHouseDeal deal = deal();

        assertThat(mapper.findHouseId(deal)).isNull();
        assertThat(mapper.insertHouse(deal)).isEqualTo(1);
        Long houseId = mapper.findHouseId(deal);

        assertThat(houseId).isNotNull();
        assertThat(mapper.insertDealIfAbsent(houseId, deal)).isEqualTo(1);
        assertThat(mapper.insertDealIfAbsent(houseId, deal)).isZero();
        assertThat(mapper.existsDeal(houseId, deal)).isTrue();
    }

    private NormalizedHouseDeal deal() {
        return new NormalizedHouseDeal(
                "1111010100", "테스트 아파트", "12-3", 2001, "아파트",
                "매매", 123456, null, 0, LocalDate.of(2026, 5, 7),
                new BigDecimal("84.95"), 10
        );
    }
}
