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

    MemberDetailResult findByNameAndEmailAndPhone(
            @Param("name") String name,
            @Param("email") String email,
            @Param("phone") String phone
    );

    int updateById(MemberUpdateParam member);

    int updatePasswordById(@Param("memberId") Long memberId, @Param("password") String password);

    int deleteById(@Param("memberId") Long memberId);
}
