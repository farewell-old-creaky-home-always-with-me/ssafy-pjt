package com.ssafy.home.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.ssafy.home.external.molit.ApartmentSaleClient;
import com.ssafy.home.external.molit.MolitHouseDealPage;
import com.ssafy.home.external.molit.MolitRawHouseDeal;
import com.ssafy.home.route.service.GraphCacheService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:house-deal-job;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:schema-test.sql",
        "spring.sql.init.data-locations=classpath:empty-data.sql",
        "spring.batch.job.enabled=false",
        "spring.batch.jdbc.initialize-schema=always",
        "mybatis.mapper-locations=classpath:/mapper/*.xml",
        "molit.apartment-sale-url=https://example.com/apartment",
        "molit.multi-family-sale-url=https://example.com/multi-family",
        "molit.service-key=test-key",
        "molit.page-size=100",
        "molit.timeout=1s",
        "molit.retry-count=1"
})
class HouseDealCollectJobTest {

    @Autowired
    @Qualifier("jobLauncher")
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("houseDealCollectJob")
    private Job job;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private ApartmentSaleClient apartmentSaleClient;

    @MockitoBean
    private GraphCacheService graphCacheService;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM batch_collection_log");
        jdbcTemplate.update("DELETE FROM house_deal");
        jdbcTemplate.update("DELETE FROM house");
        jdbcTemplate.update("DELETE FROM region_code");
        jdbcTemplate.update("""
                INSERT INTO region_code(region_code, sido_name, sigungu_name, dong_name)
                VALUES (?, ?, ?, ?)
                """, "1111010100", "서울특별시", "종로구", "청운동");
    }

    @Test
    void collectsNormalizesPersistsAndLogsHouseDeals() throws Exception {
        MolitRawHouseDeal valid = new MolitRawHouseDeal(
                "1111010100", "테스트 아파트", "12-3", "123,456",
                "2026", "5", "7", "84.95", "10", "2001"
        );
        MolitRawHouseDeal invalid = new MolitRawHouseDeal(
                "1111010100", "테스트 아파트", "12-3", "123,456",
                "2026", "5", "8", "84.95", null, "2001"
        );
        when(apartmentSaleClient.supports(com.ssafy.home.batch.domain.HouseType.APARTMENT))
                .thenReturn(true);
        when(apartmentSaleClient.fetch(eq("11110"), eq("202605"), anyInt()))
                .thenReturn(new MolitHouseDealPage(List.of(valid, valid, invalid), 3));
        JobParameters parameters = new JobParametersBuilder()
                .addString("regionCode", "11110")
                .addString("yearMonth", "202605")
                .addString("houseType", "APARTMENT")
                .addString("dealType", "SALE")
                .addLong("requestedMemberId", 1L)
                .addLong("requestedAt", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(job, parameters);

        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM house", Long.class))
                .isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM house_deal", Long.class))
                .isEqualTo(1);
        assertThat(jdbcTemplate.queryForMap("""
                SELECT collected_count, skipped_count, status
                FROM batch_collection_log
                WHERE job_execution_id = ?
                """, execution.getId()))
                .containsEntry("COLLECTED_COUNT", 1)
                .containsEntry("SKIPPED_COUNT", 2)
                .containsEntry("STATUS", "COMPLETED");
    }
}
