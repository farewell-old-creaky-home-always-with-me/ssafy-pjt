package com.ssafy.home.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.ssafy.home.auth.dto.AuthMeResponse;
import com.ssafy.home.auth.dto.LoginRequest;
import com.ssafy.home.auth.dto.LoginResponse;
import com.ssafy.home.global.auth.JwtProperties;
import com.ssafy.home.global.auth.JwtTokenProvider;
import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.exception.ErrorCode;
import com.ssafy.home.member.dto.MemberEntity;
import com.ssafy.home.member.mapper.MemberMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private JwtTokenProvider jwtTokenProvider;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(new JwtProperties(
                "test-jwt-secret-key-for-ssafy-home-project-2026",
                3_600_000
        ));
        authService = new AuthService(memberMapper, passwordEncoder, jwtTokenProvider);
    }

    @Test
    void loginIssuesAccessTokenWhenCredentialsMatch() {
        MemberEntity member = new MemberEntity();
        member.setId(1L);
        member.setName("tester");
        member.setEmail("user@example.com");
        member.setPassword("encoded");
        member.setAdmin(true);

        when(memberMapper.findByEmail("user@example.com")).thenReturn(member);
        when(passwordEncoder.matches("password1234", "encoded")).thenReturn(true);

        LoginResponse response = authService.login(new LoginRequest("user@example.com", "password1234"));

        assertThat(response.memberId()).isEqualTo(1L);
        assertThat(response.isAdmin()).isTrue();
        assertThat(response.accessToken()).isNotBlank();
        assertThat(jwtTokenProvider.getMemberId(response.accessToken())).isEqualTo(1L);
        assertThat(jwtTokenProvider.isAdmin(response.accessToken())).isTrue();
    }

    @Test
    void loginThrowsWhenCredentialsDoNotMatch() {
        MemberEntity member = new MemberEntity();
        member.setPassword("encoded");
        when(memberMapper.findByEmail("user@example.com")).thenReturn(member);
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("user@example.com", "wrong")))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(ErrorCode.AUTH_INVALID_CREDENTIALS));
    }

    @Test
    void getAuthMeReturnsAuthenticatedUserWhenTokenIsValid() {
        String token = jwtTokenProvider.createAccessToken(1L, true);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        MemberEntity member = new MemberEntity();
        member.setId(1L);
        member.setName("tester");
        member.setAdmin(true);
        when(memberMapper.findById(1L)).thenReturn(member);

        AuthMeResponse response = authService.getAuthMe(request);

        assertThat(response.isAuthenticated()).isTrue();
        assertThat(response.memberId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("tester");
        assertThat(response.isAdmin()).isTrue();
    }

    @Test
    void getAuthMeReturnsUnauthenticatedWithoutToken() {
        AuthMeResponse response = authService.getAuthMe(new MockHttpServletRequest());

        assertThat(response.isAuthenticated()).isFalse();
    }
}
