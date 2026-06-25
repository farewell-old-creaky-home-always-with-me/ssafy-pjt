package com.ssafy.home.news.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.news.mapper.dto.NewsResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@MybatisTest
@Sql("/sql/housing-content-data.sql")
class NewsMapperTest {

    @Autowired
    private NewsMapper newsMapper;

    @Test
    @DisplayName("최신 뉴스 목록을 발행일과 ID 내림차순으로 조회한다")
    void findRecent() {
        // when
        List<NewsResult> result = newsMapper.findRecent(2);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPublishedAt()).isAfterOrEqualTo(result.get(1).getPublishedAt());
        if (result.get(0).getPublishedAt().isEqual(result.get(1).getPublishedAt())) {
            assertThat(result.get(0).getId()).isGreaterThan(result.get(1).getId());
        }
    }
}
