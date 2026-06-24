package com.ssafy.home.global.config.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(properties = {
        "spring.flyway.enabled=true",
        "spring.sql.init.mode=never",
        "app.database.seed-enabled=true"
})
class DatabaseInitializationIntegrationTest {

    private static final String ORIGINAL_API_VERSION = System.getProperty("api.version");

    static {
        // Docker Engine 29 requires API 1.44 or newer for Testcontainers discovery.
        System.setProperty("api.version", "1.44");
    }

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0")
            .withCommand("--log-bin-trust-function-creators=1");

    @AfterAll
    static void restoreDockerApiVersion() {
        restoreApiVersion();
    }

    private static void restoreApiVersion() {
        if (ORIGINAL_API_VERSION == null) {
            System.clearProperty("api.version");
            return;
        }
        System.setProperty("api.version", ORIGINAL_API_VERSION);
    }

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", MYSQL::getDriverClassName);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private InitialDataLoader initialDataLoader;

    @Test
    @DisplayName("Flyway가 애플리케이션과 배치 스키마를 생성한다")
    void createsApplicationAndBatchSchemasWithFlyway() {
        // Given
        // The Spring application context has initialized the database with Flyway.

        // When
        List<String> missingTables = jdbcTemplate.queryForList("""
                SELECT t FROM (
                    SELECT 'region_code'              AS t UNION ALL
                    SELECT 'house'                         UNION ALL
                    SELECT 'house_deal'                    UNION ALL
                    SELECT 'batch_collection_log'          UNION ALL
                    SELECT 'member'                        UNION ALL
                    SELECT 'favorite_area'                 UNION ALL
                    SELECT 'member_place'                  UNION ALL
                    SELECT 'notice'                        UNION ALL
                    SELECT 'qna'                           UNION ALL
                    SELECT 'batch_report'                  UNION ALL
                    SELECT 'commercial_area'               UNION ALL
                    SELECT 'environment_info'              UNION ALL
                    SELECT 'facility'                      UNION ALL
                    SELECT 'route_request'                 UNION ALL
                    SELECT 'route_path'                    UNION ALL
                    SELECT 'BATCH_JOB_INSTANCE'            UNION ALL
                    SELECT 'BATCH_JOB_EXECUTION'           UNION ALL
                    SELECT 'BATCH_JOB_EXECUTION_PARAMS'    UNION ALL
                    SELECT 'BATCH_STEP_EXECUTION'          UNION ALL
                    SELECT 'BATCH_STEP_EXECUTION_CONTEXT'  UNION ALL
                    SELECT 'BATCH_JOB_EXECUTION_CONTEXT'   UNION ALL
                    SELECT 'BATCH_STEP_EXECUTION_SEQ'      UNION ALL
                    SELECT 'BATCH_JOB_EXECUTION_SEQ'       UNION ALL
                    SELECT 'BATCH_JOB_SEQ'
                ) AS expected
                WHERE t NOT IN (
                    SELECT table_name
                    FROM information_schema.tables
                    WHERE table_schema = DATABASE()
                )
                """, String.class);
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
        String memberPhoneNullable = jdbcTemplate.queryForObject("""
                SELECT IS_NULLABLE
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = 'member'
                  AND column_name = 'phone'
                """, String.class);

        // Then
        assertThat(missingTables)
                .as("Flyway migration did not create these expected tables")
                .isEmpty();
        assertThat(successfulMigrationCount).isEqualTo(6);
        assertThat(stepExecutionSequenceSeedCount).isEqualTo(1);
        assertThat(jobExecutionSequenceSeedCount).isEqualTo(1);
        assertThat(jobSequenceSeedCount).isEqualTo(1);
        assertThat(memberPhoneNullable).isEqualTo("NO");
    }

    @Test
    @DisplayName("초기 데이터를 Flyway 이후에 적재하고 반복 실행해도 중복되지 않는다")
    void loadsSeedDataAfterFlywayAndRemainsIdempotent() throws Exception {
        // Given
        long orphanFavoriteAreaId = Long.MAX_VALUE;
        Integer initialMemberCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM member", Integer.class);

        // When
        initialDataLoader.run(null);
        Integer repeatedMemberCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM member", Integer.class);

        // Then
        assertThat(initialMemberCount).isEqualTo(3);
        assertThat(repeatedMemberCount).isEqualTo(initialMemberCount);
        try {
            assertThatThrownBy(() -> jdbcTemplate.update("""
                    INSERT INTO favorite_area (id, member_id, region_code)
                    VALUES (?, ?, ?)
                    """, orphanFavoriteAreaId, Long.MAX_VALUE, "9999999999"))
                    .isInstanceOf(DataIntegrityViolationException.class);
        } finally {
            jdbcTemplate.update(
                    "DELETE FROM favorite_area WHERE id = ?", orphanFavoriteAreaId);
        }
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("초기 데이터 적재 중 실패하면 예외를 전파하고 이전 변경을 롤백한다")
    void propagatesSeedFailureAndRollsBackEarlierChanges() {
        // Given
        String rollbackMarker = "rollback-marker";
        jdbcTemplate.execute("DROP TRIGGER IF EXISTS test_fail_environment_info_insert");
        jdbcTemplate.execute("""
                CREATE TRIGGER test_fail_environment_info_insert
                BEFORE INSERT ON environment_info
                FOR EACH ROW
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'forced seed failure'
                """);
        jdbcTemplate.update(
                "UPDATE member SET name = ? WHERE id = 2", rollbackMarker);

        try {
            // When
            Throwable thrown = catchThrowable(() -> initialDataLoader.run(null));
            String memberName = jdbcTemplate.queryForObject(
                    "SELECT name FROM member WHERE id = 2", String.class);

            // Then
            assertThat(thrown)
                    .isInstanceOf(DataAccessException.class)
                    .hasMessageContaining("environment_info");
            assertThat(memberName).isEqualTo(rollbackMarker);
        } finally {
            jdbcTemplate.execute("DROP TRIGGER IF EXISTS test_fail_environment_info_insert");
            initialDataLoader.run(null);
        }
    }
}
