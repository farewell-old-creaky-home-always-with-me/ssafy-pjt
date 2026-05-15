package com.ssafy.home.member.mapper;

import com.ssafy.home.member.dto.MemberEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {

    boolean existsByEmail(@Param("email") String email);

    void insertMember(MemberEntity member);

    MemberEntity findById(@Param("memberId") Long memberId);

    MemberEntity findByEmail(@Param("email") String email);

    int updateMember(MemberEntity member);

    int deleteMember(@Param("memberId") Long memberId);
}
