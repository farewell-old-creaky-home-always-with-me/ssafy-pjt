package com.ssafy.home.housinginfo.mapper.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class HousingInfoResult {

    private Long id;
    private String title;
    private String content;
    private String sourceName;
    private String sourceUrl;
    private String infoType;
    private LocalDateTime publishedAt;
}
