package com.ssafy.home.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import static com.ssafy.home.global.exception.ErrorCode.AUTH_INVALID_CREDENTIALS;

import com.ssafy.home.auth.dto.AuthMeResponse;
import com.ssafy.home.auth.dto.AuthLoginRequest;
import com.ssafy.home.auth.dto.LoginResponse;
import com.ssafy.home.global.auth.SessionManager;
import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.member.mapper.MemberMapper;
import com.ssafy.home.member.mapper.dto.MemberDetailResult;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SessionManager sessionManager;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(memberMapper, passwordEncoder, sessionManager);
    }

    @Test
    @DisplayName("자격 증명이 일치하면 로그인하고 세션을 생성한다")
    void loginCreatesSessionWhenCredentialsMatch() {
        // given
        MemberDetailResult member = memberDetailResult(1L, "user@example.com", "홍길동", "encoded", true);
        given(memberMapper.findByEmail("user@example.com")).willReturn(member);
        given(passwordEncoder.matches("password1234", "encoded")).willReturn(true);
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        LoginResponse response = authService.login(
                new AuthLoginRequest("user@example.com", "password1234"), request);

        // then
        assertThat(response.memberId()).isEqualTo(1L);
        assertThat(response.isAdmin()).isTrue();
        assertThat(request.getSession(false)).isNotNull();
        assertThat(request.getSession(false).getAttribute("memberId")).isEqualTo(1L);
        assertThat(request.getSession(false).getAttribute("isAdmin")).isEqualTo(true);
    }

    @Test
    @DisplayName("자격 증명이 일치하지 않으면 로그인 예외가 발생한다")
    void loginThrowsWhenCredentialsDoNotMatch() {
        // given
        MemberDetailResult member = new MemberDetailResult();
        member.setPassword("encoded");
        given(memberMapper.findByEmail("user@example.com")).willReturn(member);
        given(passwordEncoder.matches("wrong", "encoded")).willReturn(false);

        // when / then
        assertThatThrownBy(() -> authService.login(
                new AuthLoginRequest("user@example.com", "wrong"), new MockHttpServletRequest()))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(AUTH_INVALID_CREDENTIALS))
                .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다");
    }

    @Test
    @DisplayName("세션 회원이 DB에 없으면 미인증 응답을 반환하고 세션을 무효화한다")
    void getAuthMeReturnsUnauthenticatedWhenSessionMemberIsMissing() {
        // given
        given(sessionManager.findCurrentMemberId()).willReturn(Optional.of(1L));
        given(memberMapper.findById(1L)).willReturn(null);

        // when
        AuthMeResponse response = authService.getAuthMe();

        // then
        assertThat(response.isAuthenticated()).isFalse();
        verify(sessionManager).invalidateCurrentSession();
    }

    private MemberDetailResult memberDetailResult(
            Long id, String email, String name, String password, boolean admin
    ) {
        MemberDetailResult member = new MemberDetailResult();
        member.setId(id);
        member.setEmail(email);
        member.setName(name);
        member.setPassword(password);
        member.setAdmin(admin);
        return member;
    }
}
