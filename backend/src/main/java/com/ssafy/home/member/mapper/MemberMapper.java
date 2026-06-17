package com.ssafy.home.member.mapper;

import com.ssafy.home.member.mapper.dto.MemberCreateParam;
import com.ssafy.home.member.mapper.dto.MemberDetailResult;
import com.ssafy.home.member.mapper.dto.MemberUpdateParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {

    boolean existsByEmail(@Param("email") String email);

    void insert(MemberCreateParam member);

    MemberDetailResult findById(@Param("memberId") Long memberId);

    MemberDetailResult findByEmail(@Param("email") String email);

    int update(MemberUpdateParam member);

    int delete(@Param("memberId") Long memberId);
}
