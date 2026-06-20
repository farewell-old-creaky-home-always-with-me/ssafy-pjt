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
        Integer tableCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = DATABASE()
                  AND table_name IN ('member', 'BATCH_JOB_INSTANCE')
                """, Integer.class);
        Integer successfulMigrationCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM flyway_schema_history
                WHERE success = 1
                """, Integer.class);

        // Then
        assertThat(tableCount).isEqualTo(2);
        assertThat(successfulMigrationCount).isEqualTo(2);
    }
}
