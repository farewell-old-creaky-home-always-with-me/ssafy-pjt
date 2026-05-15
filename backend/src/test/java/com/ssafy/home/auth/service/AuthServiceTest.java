package com.ssafy.home.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ssafy.home.auth.dto.AuthMeResponse;
import com.ssafy.home.auth.dto.LoginRequest;
import com.ssafy.home.auth.dto.LoginResponse;
import com.ssafy.home.global.exception.UnauthorizedException;
import com.ssafy.home.member.dto.MemberEntity;
import com.ssafy.home.member.mapper.MemberMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(memberMapper, passwordEncoder);
    }

    @Test
    void loginCreatesSessionWhenCredentialsMatch() {
        MemberEntity member = new MemberEntity();
        member.setMemberId(1L);
        member.setName("홍길동");
        member.setEmail("user@example.com");
        member.setPassword("encoded");
        member.setAdmin(true);

        when(memberMapper.findByEmail("user@example.com")).thenReturn(member);
        when(passwordEncoder.matches("password1234", "encoded")).thenReturn(true);

        MockHttpServletRequest request = new MockHttpServletRequest();

        LoginResponse response = authService.login(new LoginRequest("user@example.com", "password1234"), request);

        assertThat(response.memberId()).isEqualTo(1L);
        assertThat(response.isAdmin()).isTrue();
        assertThat(request.getSession(false)).isNotNull();
        assertThat(request.getSession(false).getAttribute("memberId")).isEqualTo(1L);
        assertThat(request.getSession(false).getAttribute("isAdmin")).isEqualTo(true);
    }

    @Test
    void loginThrowsWhenCredentialsDoNotMatch() {
        MemberEntity member = new MemberEntity();
        member.setPassword("encoded");
        when(memberMapper.findByEmail("user@example.com")).thenReturn(member);
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("user@example.com", "wrong"), new MockHttpServletRequest()))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다");
    }

    @Test
    void getAuthMeReturnsUnauthenticatedWhenSessionMemberIsMissing() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("memberId", 1L);
        when(memberMapper.findById(1L)).thenReturn(null);

        AuthMeResponse response = authService.getAuthMe(session);

        assertThat(response.isAuthenticated()).isFalse();
        assertThat(session.isInvalid()).isTrue();
    }
}
