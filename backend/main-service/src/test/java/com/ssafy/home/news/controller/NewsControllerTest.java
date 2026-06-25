package com.ssafy.home.news.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ssafy.home.news.dto.NewsResponse;
import com.ssafy.home.news.service.NewsService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class NewsControllerTest {

    private NewsService newsService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        newsService = org.mockito.Mockito.mock(NewsService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new NewsController(newsService))
                .setMessageConverters(jsonMessageConverter())
                .build();
    }

    @Test
    @DisplayName("뉴스 목록을 조회한다")
    void getNews() throws Exception {
        // given
        given(newsService.getNews(10)).willReturn(List.of(newsResponse()));

        // when / then
        mockMvc.perform(get("/api/news")
                        .param("limit", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("서울 주택 공급 확대"))
                .andExpect(jsonPath("$[0].summary").value("공급 정책 요약"))
                .andExpect(jsonPath("$[0].sourceName").value("국토일보"))
                .andExpect(jsonPath("$[0].sourceUrl").value("https://example.com/news/1"))
                .andExpect(jsonPath("$[0].category").value("POLICY"))
                .andExpect(jsonPath("$[0].publishedAt").value("2026-06-25T09:30:00"));
    }

    private NewsResponse newsResponse() {
        return new NewsResponse(
                1L,
                "서울 주택 공급 확대",
                "공급 정책 요약",
                "국토일보",
                "https://example.com/news/1",
                "POLICY",
                LocalDateTime.of(2026, 6, 25, 9, 30)
        );
    }

    private MappingJackson2HttpMessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }
}
