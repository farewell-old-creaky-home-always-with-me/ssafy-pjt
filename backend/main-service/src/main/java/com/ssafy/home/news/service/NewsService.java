package com.ssafy.home.news.service;

import com.ssafy.home.news.dto.NewsResponse;
import com.ssafy.home.news.mapper.NewsMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NewsService {

    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 50;

    private final NewsMapper newsMapper;

    @Transactional(readOnly = true)
    public List<NewsResponse> getNews(Integer limit) {
        return newsMapper.findRecent(normalizeLimit(limit))
                .stream()
                .map(NewsResponse::from)
                .toList();
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }
}
