package com.ssafy.home.news.mapper.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NewsResult {

    private Long id;
    private String title;
    private String summary;
    private String sourceName;
    private String sourceUrl;
    private String category;
    private LocalDateTime publishedAt;
}
