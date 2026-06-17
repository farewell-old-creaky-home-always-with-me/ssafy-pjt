package com.ssafy.home.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ssafy.home.global.exception.CustomException;
import static com.ssafy.home.global.exception.ErrorCode.MEMBER_DUPLICATE_EMAIL;
import com.ssafy.home.member.dto.MemberCreateRequest;
import com.ssafy.home.member.dto.MemberDetailResponse;
import com.ssafy.home.member.dto.MemberUpdateRequest;
import com.ssafy.home.member.mapper.MemberMapper;
import com.ssafy.home.member.mapper.dto.MemberCreateParam;
import com.ssafy.home.member.mapper.dto.MemberDetailResult;
import com.ssafy.home.member.mapper.dto.MemberUpdateParam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = new MemberService(memberMapper, passwordEncoder);
    }

    @Test
    void createMemberThrowsWhenEmailAlreadyExists() {
        when(memberMapper.existsByEmail("user@example.com")).thenReturn(true);

        assertThatThrownBy(() -> memberService.createMember(new MemberCreateRequest("user@example.com", "password1234", "홍길동")))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(MEMBER_DUPLICATE_EMAIL))
                .hasMessage("이미 사용 중인 이메일입니다");
    }

    @Test
    void createMemberEncodesPasswordAndReturnsResponse() {
        when(memberMapper.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password1234")).thenReturn("encoded-password");
        doAnswer(invocation -> {
            MemberCreateParam param = invocation.getArgument(0);
            param.setId(10L);
            return null;
        }).when(memberMapper).insert(any(MemberCreateParam.class));

        MemberDetailResult saved = new MemberDetailResult();
        saved.setId(10L);
        saved.setEmail("user@example.com");
        saved.setName("홍길동");
        when(memberMapper.findById(10L)).thenReturn(saved);

        MemberDetailResponse response = memberService.createMember(new MemberCreateRequest("user@example.com", "password1234", "홍길동"));

        assertThat(response.memberId()).isEqualTo(10L);
        assertThat(response.email()).isEqualTo("user@example.com");
        verify(passwordEncoder).encode("password1234");
    }

    @Test
    void updateReplacesNameAndPassword() {
        MemberDetailResult member = new MemberDetailResult();
        member.setId(1L);
        member.setName("기존 이름");
        member.setPassword("old");
        when(memberMapper.findById(1L)).thenReturn(member);
        when(passwordEncoder.encode("new-password")).thenReturn("encoded");

        var response = memberService.updateMyMember(1L, new MemberUpdateRequest("새 이름", "new-password"));

        assertThat(response.memberId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("새 이름");
        verify(memberMapper).updateById(any(MemberUpdateParam.class));
    }
}
