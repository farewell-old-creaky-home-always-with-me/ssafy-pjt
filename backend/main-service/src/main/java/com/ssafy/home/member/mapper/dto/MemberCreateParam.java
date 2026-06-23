package com.ssafy.home.member.mapper.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberCreateParam {

    private Long id;
    private String email;
    private String password;
    private String name;
    private boolean isAdmin;
}
