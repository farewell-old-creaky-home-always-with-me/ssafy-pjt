package com.ssafy.home.auth.service;

import static com.ssafy.home.global.exception.ErrorCode.AUTH_INVALID_CREDENTIALS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ssafy.home.auth.dto.AuthLoginRequest;
import com.ssafy.home.auth.dto.AuthMeResponse;
import com.ssafy.home.auth.dto.LoginResponse;
import com.ssafy.home.global.auth.JwtProperties;
import com.ssafy.home.global.auth.JwtTokenProvider;
import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.member.mapper.MemberMapper;
import com.ssafy.home.member.mapper.dto.MemberDetailResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import jakarta.servlet.http.Cookie;
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
    @DisplayName("자격 증명이 일치하면 JWT 액세스 토큰을 발급한다")
    void loginIssuesAccessTokenWhenCredentialsMatch() {
        // given
        MemberDetailResult member = memberDetailResult(1L, "user@example.com", "tester", "encoded", true);
        given(memberMapper.findByEmail("user@example.com")).willReturn(member);
        given(passwordEncoder.matches("password1234", "encoded")).willReturn(true);

        // when
        LoginResponse response = authService.login(new AuthLoginRequest("user@example.com", "password1234"));

        // then
        assertThat(response.memberId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("tester");
        assertThat(response.isAdmin()).isTrue();
        assertThat(response.accessToken()).isNotBlank();
        assertThat(jwtTokenProvider.getMemberId(response.accessToken())).isEqualTo(1L);
        assertThat(jwtTokenProvider.isAdmin(response.accessToken())).isTrue();
    }

    @Test
    @DisplayName("자격 증명이 일치하지 않으면 로그인 예외가 발생한다")
    void loginThrowsWhenCredentialsDoNotMatch() {
        // given
        MemberDetailResult member = memberDetailResult(1L, "user@example.com", "tester", "encoded", false);
        given(memberMapper.findByEmail("user@example.com")).willReturn(member);
        given(passwordEncoder.matches("wrong", "encoded")).willReturn(false);

        // when / then
        assertThatThrownBy(() -> authService.login(new AuthLoginRequest("user@example.com", "wrong")))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(AUTH_INVALID_CREDENTIALS));
    }

    @Test
    @DisplayName("유효한 JWT 토큰이면 현재 회원 정보를 반환한다")
    void getAuthMeReturnsAuthenticatedMemberWhenTokenIsValid() {
        // given
        String token = jwtTokenProvider.createAccessToken(1L, true);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        given(memberMapper.findById(1L))
                .willReturn(memberDetailResult(1L, "user@example.com", "tester", "encoded", true));

        // when
        AuthMeResponse response = authService.getAuthMe(request);

        // then
        assertThat(response.isAuthenticated()).isTrue();
        assertThat(response.memberId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("tester");
        assertThat(response.isAdmin()).isTrue();
    }

    @Test
    @DisplayName("유효한 JWT 쿠키이면 현재 회원 정보를 반환한다")
    void getAuthMeReturnsAuthenticatedMemberWhenCookieIsValid() {
        // given
        String token = jwtTokenProvider.createAccessToken(1L, true);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("access_token", token));
        given(memberMapper.findById(1L))
                .willReturn(memberDetailResult(1L, "user@example.com", "tester", "encoded", true));

        // when
        AuthMeResponse response = authService.getAuthMe(request);

        // then
        assertThat(response.isAuthenticated()).isTrue();
        assertThat(response.memberId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("토큰이 없으면 게스트 응답을 반환한다")
    void getAuthMeReturnsGuestWithoutToken() {
        // when
        AuthMeResponse response = authService.getAuthMe(new MockHttpServletRequest());

        // then
        assertThat(response.isAuthenticated()).isFalse();
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
