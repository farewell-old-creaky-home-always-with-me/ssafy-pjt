package com.ssafy.home.member.mapper.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberUpdateParam {

    private Long id;
    private String name;
    private String password;
}
