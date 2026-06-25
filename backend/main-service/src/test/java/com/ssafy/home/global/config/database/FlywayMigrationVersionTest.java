package com.ssafy.home.global.config.database;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FlywayMigrationVersionTest {

    private static final String MIGRATION_DIR =
            "src/main/resources/db/migration";

    @Test
    @DisplayName("Flyway 마이그레이션 버전 번호가 중복되지 않는다")
    void noDuplicateMigrationVersions() {
        File dir = new File(MIGRATION_DIR);
        String[] files = dir.list((d, name) -> name.matches("V\\d+__.*\\.sql"));
        assertThat(files)
                .as("Migration directory not found: " + dir.getAbsolutePath())
                .isNotNull();

        Map<String, List<String>> byVersion = Arrays.stream(files)
                .collect(Collectors.groupingBy(name -> name.replaceAll("^V(\\d+)__.*", "$1")));

        Map<String, List<String>> duplicates = byVersion.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertThat(duplicates)
                .as("Duplicate Flyway versions found: " + duplicates)
                .isEmpty();
    }
}
