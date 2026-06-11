package com.ssafy.home.stats.controller;

import com.ssafy.home.stats.dto.StatsResponse;
import com.ssafy.home.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController implements StatsApiDocs {

    private final StatsService statsService;

    @GetMapping
    @Override
    public StatsResponse getStats() {
        return statsService.getStats();
    }
}
