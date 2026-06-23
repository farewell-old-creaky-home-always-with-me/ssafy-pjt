package com.ssafy.home.qna.mapper.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QnaResult {

    private Long id;
    private Long memberId;
    private String title;
    private String content;
    private String authorName;
    private String answer;
    private LocalDateTime answeredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
