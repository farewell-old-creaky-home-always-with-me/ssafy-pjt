package com.ssafy.home.news.controller;

import com.ssafy.home.news.dto.NewsResponse;
import com.ssafy.home.news.service.NewsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<List<NewsResponse>> getNews(@RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(newsService.getNews(limit));
    }
}
