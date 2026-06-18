package com.ssafy.home.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.ssafy.home.external.vworld.VworldLegalRegionClient;
import com.ssafy.home.external.vworld.VworldRawRegion;
import com.ssafy.home.external.vworld.VworldRegionPage;
import com.ssafy.home.external.vworld.VworldSidoCodes;
import com.ssafy.home.route.service.GraphCacheService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
        "spring.datasource.url=jdbc:h2:mem:region-code-job;MODE=MySQL;DB_CLOSE_DELAY=-1",
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
        "molit.retry-count=1",
        "vworld.data-url=http://localhost/vworld",
        "vworld.api-key=test-key",
        "vworld.domain=localhost",
        "vworld.legal-emd-layer=LT_C_ADLEGAL_EMD",
        "vworld.page-size=100",
        "vworld.timeout=1s",
        "vworld.retry-count=0",
        "vworld.sido-retry-count=1",
        "vworld.skip-limit=1000"
})
class RegionCodeCollectJobTest {

    @Autowired
    @Qualifier("jobLauncher")
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("regionCodeCollectJob")
    private Job job;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private VworldLegalRegionClient vworldLegalRegionClient;

    @MockitoBean
    private GraphCacheService graphCacheService;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM batch_collection_log");
        jdbcTemplate.update("DELETE FROM region_code");
    }

    @Test
    @DisplayName("전국 법정동을 수집해 region_code에 저장한다")
    void collectsAndPersistsRegionCodes() throws Exception {
        // given
        VworldRawRegion valid = rawRegion("1111010100", false);
        VworldRawRegion invalid = rawRegion("11110", false);
        given(vworldLegalRegionClient.fetch(eq("11"), anyInt()))
                .willReturn(new VworldRegionPage(List.of(valid, invalid), 2));
        stubEmptySidoResponsesExcept("11");

        JobParameters parameters = new JobParametersBuilder()
                .addString("syncScope", "FULL")
                .addLong("requestedMemberId", 1L, false)
                .addLong("requestedAt", System.currentTimeMillis(), false)
                .toJobParameters();

        // when
        JobExecution execution = jobLauncher.run(job, parameters);

        // then
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM region_code", Long.class))
                .isEqualTo(1);
        assertThat(jdbcTemplate.queryForMap("""
                SELECT data_type, collected_count, skipped_count, status
                FROM batch_collection_log
                WHERE job_execution_id = ?
                """, execution.getId()))
                .containsEntry("DATA_TYPE", "REGION_CODE")
                .containsEntry("COLLECTED_COUNT", 1)
                .containsEntry("SKIPPED_COUNT", 1)
                .containsEntry("STATUS", "COMPLETED");
    }

    private void stubEmptySidoResponsesExcept(String excludedSidoCode) {
        for (String sidoCode : VworldSidoCodes.ALL) {
            if (sidoCode.equals(excludedSidoCode)) {
                continue;
            }
            given(vworldLegalRegionClient.fetch(eq(sidoCode), anyInt()))
                    .willReturn(new VworldRegionPage(List.of(), 0));
        }
    }

    private VworldRawRegion rawRegion(String regionCode, boolean abolished) {
        return new VworldRawRegion(
                regionCode, "서울특별시", "종로구", "청운동", abolished
        );
    }
}
