package com.ssafy.home.member.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.member.mapper.dto.MemberCreateParam;
import com.ssafy.home.member.mapper.dto.MemberDetailResult;
import com.ssafy.home.member.mapper.dto.MemberUpdateParam;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@MybatisTest
@Sql("/sql/member-data.sql")
class MemberMapperTest {

    @Autowired
    private MemberMapper memberMapper;

    @Test
    @DisplayName("ID로 회원을 조회한다")
    void findById() {
        // when
        MemberDetailResult found = memberMapper.findById(1L);

        // then
        assertThat(found.getEmail()).isEqualTo("user@example.com");
        assertThat(found.getName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("이메일로 회원 존재 여부를 확인한다")
    void existsByEmail() {
        // when / then
        assertThat(memberMapper.existsByEmail("user@example.com")).isTrue();
        assertThat(memberMapper.existsByEmail("missing@example.com")).isFalse();
    }

    @Test
    @DisplayName("회원을 등록하고 수정·삭제한다")
    void insertUpdateAndDelete() {
        // given
        MemberCreateParam createParam = new MemberCreateParam();
        createParam.setEmail("new@example.com");
        createParam.setPassword("encoded-password");
        createParam.setName("신규회원");

        // when
        memberMapper.insert(createParam);

        // then
        assertThat(createParam.getId()).isNotNull();
        assertThat(memberMapper.existsByEmail("new@example.com")).isTrue();

        // given
        MemberUpdateParam updateParam = new MemberUpdateParam();
        updateParam.setId(createParam.getId());
        updateParam.setName("수정회원");
        updateParam.setPassword("new-encoded-password");

        // when
        int updated = memberMapper.updateById(updateParam);

        // then
        assertThat(updated).isEqualTo(1);
        assertThat(memberMapper.findById(createParam.getId()).getName()).isEqualTo("수정회원");

        // when
        int deleted = memberMapper.deleteById(createParam.getId());

        // then
        assertThat(deleted).isEqualTo(1);
        assertThat(memberMapper.findById(createParam.getId())).isNull();
    }
}
