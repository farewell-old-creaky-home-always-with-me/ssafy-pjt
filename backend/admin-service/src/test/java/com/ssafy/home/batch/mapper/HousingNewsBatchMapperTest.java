package com.ssafy.home.batch.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.batch.domain.HousingInfoType;
import com.ssafy.home.batch.domain.HousingNewsCategory;
import com.ssafy.home.batch.domain.NormalizedHousingInfo;
import com.ssafy.home.batch.domain.NormalizedHousingNews;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

@MybatisTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql("/sql/housing-content-batch-data.sql")
class HousingNewsBatchMapperTest {

    @Autowired
    private HousingNewsBatchMapper mapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("같은 출처 URL의 주거 뉴스를 upsert한다")
    void upsertNewsUpdatesBySourceUrl() {
        // Given
        NormalizedHousingNews original = news("시장 동향", "요약");
        NormalizedHousingNews updated = news("시장 동향 수정", "요약 수정");
        mapper.upsertNews(original);

        // When
        int affected = mapper.upsertNews(updated);

        // Then
        assertThat(affected).isGreaterThanOrEqualTo(1);
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM housing_news WHERE source_url = ?",
                Integer.class,
                "https://news.example.com/unique"
        );
        String title = jdbcTemplate.queryForObject(
                "SELECT title FROM housing_news WHERE source_url = ?",
                String.class,
                "https://news.example.com/unique"
        );
        assertThat(count).isEqualTo(1);
        assertThat(title).isEqualTo("시장 동향 수정");
    }

    @Test
    @DisplayName("같은 출처 URL의 주거 정보를 upsert한다")
    void upsertInfoUpdatesBySourceUrl() {
        // Given
        NormalizedHousingInfo original = info("생활 정보", "본문");
        NormalizedHousingInfo updated = info("생활 정보 수정", "본문 수정");
        mapper.upsertInfo(original);

        // When
        int affected = mapper.upsertInfo(updated);

        // Then
        assertThat(affected).isGreaterThanOrEqualTo(1);
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM housing_info WHERE source_url = ?",
                Integer.class,
                "https://info.example.com/unique"
        );
        String title = jdbcTemplate.queryForObject(
                "SELECT title FROM housing_info WHERE source_url = ?",
                String.class,
                "https://info.example.com/unique"
        );
        assertThat(count).isEqualTo(1);
        assertThat(title).isEqualTo("생활 정보 수정");
    }

    private NormalizedHousingNews news(String title, String summary) {
        return new NormalizedHousingNews(
                title,
                summary,
                "https://news.example.com/unique",
                "국토부",
                HousingNewsCategory.MARKET,
                LocalDateTime.of(2026, 6, 24, 9, 0)
        );
    }

    private NormalizedHousingInfo info(String title, String content) {
        return new NormalizedHousingInfo(
                title,
                content,
                "https://info.example.com/unique",
                "서울시",
                HousingInfoType.LIVING,
                LocalDateTime.of(2026, 6, 24, 10, 0)
        );
    }
}
