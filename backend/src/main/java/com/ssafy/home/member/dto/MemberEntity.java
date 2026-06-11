package com.ssafy.home.member.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberEntity {

    private Long id;
    private String email;
    private String password;
    private String name;
    private boolean isAdmin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
