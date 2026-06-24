package com.ssafy.home.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.verify;

import static com.ssafy.home.global.exception.ErrorCode.MEMBER_DUPLICATE_EMAIL;
import static com.ssafy.home.global.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.member.dto.MemberCreateRequest;
import com.ssafy.home.member.dto.MemberDetailResponse;
import com.ssafy.home.member.dto.MemberPasswordResetRequest;
import com.ssafy.home.member.dto.MemberUpdateRequest;
import com.ssafy.home.member.mapper.MemberMapper;
import com.ssafy.home.member.mapper.dto.MemberCreateParam;
import com.ssafy.home.member.mapper.dto.MemberDetailResult;
import com.ssafy.home.member.mapper.dto.MemberUpdateParam;
import com.ssafy.home.member.service.mail.PasswordResetMailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @Mock
    private TemporaryPasswordGenerator temporaryPasswordGenerator;

    @Mock
    private PasswordResetMailSender passwordResetMailSender;

    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = new MemberService(memberMapper, passwordEncoder, temporaryPasswordGenerator, passwordResetMailSender);
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 가입하면 예외가 발생한다")
    void createMemberThrowsWhenEmailAlreadyExists() {
        // given
        given(memberMapper.existsByEmail("user@example.com")).willReturn(true);

        // when / then
        assertThatThrownBy(() -> memberService.createMember(
                new MemberCreateRequest("user@example.com", "password1234", "홍길동", "010-1234-5678")))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(MEMBER_DUPLICATE_EMAIL))
                .hasMessage("이미 사용 중인 이메일입니다");
    }

    @Test
    @DisplayName("회원 가입 시 비밀번호를 인코딩하고 응답을 반환한다")
    void createMemberEncodesPasswordAndReturnsResponse() {
        // given
        given(memberMapper.existsByEmail("user@example.com")).willReturn(false);
        given(passwordEncoder.encode("password1234")).willReturn("encoded-password");
        willAnswer(invocation -> {
            MemberCreateParam param = invocation.getArgument(0);
            param.setId(10L);
            return null;
        }).given(memberMapper).insert(any(MemberCreateParam.class));
        given(memberMapper.findById(10L)).willReturn(memberDetailResult(10L, "user@example.com", "홍길동"));

        // when
        MemberDetailResponse response = memberService.createMember(
                new MemberCreateRequest("user@example.com", "password1234", "홍길동", "010-1234-5678"));

        // then
        assertThat(response.memberId()).isEqualTo(10L);
        assertThat(response.email()).isEqualTo("user@example.com");
        verify(passwordEncoder).encode("password1234");
    }

    @Test
    @DisplayName("회원 정보 수정 시 이름과 비밀번호를 갱신한다")
    void updateReplacesNameAndPassword() {
        // given
        given(memberMapper.findById(1L)).willReturn(memberDetailResult(1L, "user@example.com", "기존 이름"));
        given(passwordEncoder.encode("new-password")).willReturn("encoded");

        // when
        var response = memberService.updateMyMember(1L, new MemberUpdateRequest("새 이름", "new-password", "010-1111-2222"));

        // then
        assertThat(response.memberId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("새 이름");
        verify(memberMapper).updateById(any(MemberUpdateParam.class));
    }

    @Test
    @DisplayName("이름·이메일·전화번호가 일치하면 임시 비밀번호를 저장하고 메일을 발송한다")
    void resetPasswordUpdatesPasswordAndSendsEmail() {
        // given
        MemberPasswordResetRequest request = new MemberPasswordResetRequest(
                "홍길동",
                "user@example.com",
                "010-1234-5678"
        );
        MemberDetailResult member = memberDetailResult(1L, "user@example.com", "홍길동");
        given(memberMapper.findByNameAndEmailAndPhone("홍길동", "user@example.com", "010-1234-5678"))
                .willReturn(member);
        given(temporaryPasswordGenerator.generate()).willReturn("Temp1234!");
        given(passwordEncoder.encode("Temp1234!")).willReturn("encoded-temp-password");

        // when
        memberService.resetPassword(request);

        // then
        verify(memberMapper).updatePasswordById(1L, "encoded-temp-password");
        verify(passwordResetMailSender).send("user@example.com", "홍길동", "Temp1234!");
    }

    @Test
    @DisplayName("비밀번호 재설정 대상 회원이 없으면 예외가 발생한다")
    void resetPasswordThrowsWhenMemberNotFound() {
        // given
        MemberPasswordResetRequest request = new MemberPasswordResetRequest(
                "홍길동",
                "missing@example.com",
                "010-1234-5678"
        );
        given(memberMapper.findByNameAndEmailAndPhone("홍길동", "missing@example.com", "010-1234-5678"))
                .willReturn(null);

        // when / then
        assertThatThrownBy(() -> memberService.resetPassword(request))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(MEMBER_NOT_FOUND));
    }

    private MemberDetailResult memberDetailResult(Long id, String email, String name) {
        MemberDetailResult member = new MemberDetailResult();
        member.setId(id);
        member.setEmail(email);
        member.setName(name);
        member.setPhone("010-1234-5678");
        return member;
    }
}
