package com.ssafy.home.member.mapper.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDetailResult {

    private Long id;
    private String email;
    private String password;
    private String name;
    private boolean isAdmin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
