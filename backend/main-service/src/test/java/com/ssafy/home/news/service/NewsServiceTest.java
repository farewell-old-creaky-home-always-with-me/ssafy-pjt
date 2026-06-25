package com.ssafy.home.news.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ssafy.home.news.dto.NewsResponse;
import com.ssafy.home.news.mapper.NewsMapper;
import com.ssafy.home.news.mapper.dto.NewsResult;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NewsServiceTest {

    @Mock
    private NewsMapper newsMapper;

    private NewsService newsService;

    @BeforeEach
    void setUp() {
        newsService = new NewsService(newsMapper);
    }

    @Test
    @DisplayName("null limit은 기본값 20으로 조회한다")
    void nullLimitUsesDefaultLimit() {
        // given
        given(newsMapper.findRecent(20)).willReturn(List.of(newsResult()));

        // when
        List<NewsResponse> result = newsService.getNews(null);

        // then
        assertThat(result).hasSize(1);
        then(newsMapper).should().findRecent(20);
    }

    @Test
    @DisplayName("0 이하 limit은 기본값 20으로 조회한다")
    void nonPositiveLimitUsesDefaultLimit() {
        // given
        given(newsMapper.findRecent(20)).willReturn(List.of(newsResult()));

        // when
        List<NewsResponse> result = newsService.getNews(0);

        // then
        assertThat(result).hasSize(1);
        then(newsMapper).should().findRecent(20);
    }

    @Test
    @DisplayName("50을 초과한 limit은 50으로 제한한다")
    void limitGreaterThanMaxIsCapped() {
        // given
        given(newsMapper.findRecent(50)).willReturn(List.of(newsResult()));

        // when
        List<NewsResponse> result = newsService.getNews(100);

        // then
        assertThat(result).hasSize(1);
        then(newsMapper).should().findRecent(50);
    }

    @Test
    @DisplayName("뉴스 조회 결과를 응답 DTO로 변환한다")
    void mapsResultToResponse() {
        // given
        given(newsMapper.findRecent(1)).willReturn(List.of(newsResult()));

        // when
        List<NewsResponse> result = newsService.getNews(1);

        // then
        assertThat(result)
                .singleElement()
                .satisfies(response -> {
                    assertThat(response.id()).isEqualTo(1L);
                    assertThat(response.title()).isEqualTo("서울 주택 공급 확대");
                    assertThat(response.summary()).isEqualTo("공급 정책 요약");
                    assertThat(response.sourceName()).isEqualTo("국토일보");
                    assertThat(response.sourceUrl()).isEqualTo("https://example.com/news/1");
                    assertThat(response.category()).isEqualTo("POLICY");
                    assertThat(response.publishedAt()).isEqualTo(LocalDateTime.of(2026, 6, 25, 9, 30));
                });
    }

    private NewsResult newsResult() {
        NewsResult result = new NewsResult();
        result.setId(1L);
        result.setTitle("서울 주택 공급 확대");
        result.setSummary("공급 정책 요약");
        result.setSourceName("국토일보");
        result.setSourceUrl("https://example.com/news/1");
        result.setCategory("POLICY");
        result.setPublishedAt(LocalDateTime.of(2026, 6, 25, 9, 30));
        return result;
    }
}
