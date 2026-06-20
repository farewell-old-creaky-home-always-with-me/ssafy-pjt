package com.ssafy.home.global.config.database;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(properties = {
        "spring.flyway.enabled=true",
        "spring.sql.init.mode=never",
        "spring.batch.job.enabled=false",
        "spring.batch.jdbc.initialize-schema=never",
        "app.database.seed-enabled=false"
})
class DatabaseInitializationIntegrationTest {

    static {
        // Docker Engine 29 requires API 1.44 or newer for Testcontainers discovery.
        System.setProperty("api.version", "1.44");
    }

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0");

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", MYSQL::getDriverClassName);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Flyway가 애플리케이션과 배치 스키마를 생성한다")
    void createsApplicationAndBatchSchemasWithFlyway() {
        // Given
        // The Spring application context has initialized the database with Flyway.

        // When
        Integer managedTableCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = DATABASE()
                  AND table_name IN (
                      'region_code',
                      'house',
                      'house_deal',
                      'batch_collection_log',
                      'member',
                      'favorite_area',
                      'member_place',
                      'notice',
                      'commercial_area',
                      'environment_info',
                      'facility',
                      'route_request',
                      'route_path',
                      'BATCH_JOB_INSTANCE',
                      'BATCH_JOB_EXECUTION',
                      'BATCH_JOB_EXECUTION_PARAMS',
                      'BATCH_STEP_EXECUTION',
                      'BATCH_STEP_EXECUTION_CONTEXT',
                      'BATCH_JOB_EXECUTION_CONTEXT',
                      'BATCH_STEP_EXECUTION_SEQ',
                      'BATCH_JOB_EXECUTION_SEQ',
                      'BATCH_JOB_SEQ'
                  )
                """, Integer.class);
        Integer successfulMigrationCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM flyway_schema_history
                WHERE success = 1
                """, Integer.class);
        Integer stepExecutionSequenceSeedCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM BATCH_STEP_EXECUTION_SEQ
                WHERE ID = 0 AND UNIQUE_KEY = '0'
                """, Integer.class);
        Integer jobExecutionSequenceSeedCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM BATCH_JOB_EXECUTION_SEQ
                WHERE ID = 0 AND UNIQUE_KEY = '0'
                """, Integer.class);
        Integer jobSequenceSeedCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM BATCH_JOB_SEQ
                WHERE ID = 0 AND UNIQUE_KEY = '0'
                """, Integer.class);

        // Then
        assertThat(managedTableCount).isEqualTo(22);
        assertThat(successfulMigrationCount).isEqualTo(2);
        assertThat(stepExecutionSequenceSeedCount).isEqualTo(1);
        assertThat(jobExecutionSequenceSeedCount).isEqualTo(1);
        assertThat(jobSequenceSeedCount).isEqualTo(1);
    }
}
