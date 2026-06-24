package com.ssafy.home.global.interceptor;

import static com.ssafy.home.global.exception.ErrorCode.AUTH_FORBIDDEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ssafy.home.global.config.AdminCsrfProperties;
import com.ssafy.home.global.exception.CustomException;
import jakarta.servlet.http.Cookie;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class CookieCsrfInterceptorTest {

    private CookieCsrfInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new CookieCsrfInterceptor(new AdminCsrfProperties(List.of("http://localhost:5174")));
    }

    @Test
    @DisplayName("쿠키 인증 상태 변경 요청은 허용된 Origin이면 통과한다")
    void unsafeCookieRequestPassesWithAllowedOrigin() {
        // given
        MockHttpServletRequest request = unsafeCookieRequest();
        request.addHeader(HttpHeaders.ORIGIN, "http://localhost:5174");

        // when
        boolean result = interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("쿠키 인증 상태 변경 요청은 허용된 Referer이면 통과한다")
    void unsafeCookieRequestPassesWithAllowedReferer() {
        // given
        MockHttpServletRequest request = unsafeCookieRequest();
        request.addHeader(HttpHeaders.REFERER, "http://localhost:5174/admin/notices");

        // when
        boolean result = interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("쿠키 인증 상태 변경 요청은 Origin과 Referer가 없으면 차단한다")
    void unsafeCookieRequestFailsWithoutOriginAndReferer() {
        // given
        MockHttpServletRequest request = unsafeCookieRequest();

        // when / then
        assertThatThrownBy(() -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object()))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(AUTH_FORBIDDEN));
    }

    @Test
    @DisplayName("쿠키 인증 상태 변경 요청은 허용되지 않은 Origin이면 차단한다")
    void unsafeCookieRequestFailsWithDisallowedOrigin() {
        // given
        MockHttpServletRequest request = unsafeCookieRequest();
        request.addHeader(HttpHeaders.ORIGIN, "http://evil.example");

        // when / then
        assertThatThrownBy(() -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object()))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> assertThat(((CustomException) exception).getErrorCode())
                        .isEqualTo(AUTH_FORBIDDEN));
    }

    @Test
    @DisplayName("Bearer 기반 상태 변경 요청은 Origin 검증 대상이 아니다")
    void unsafeBearerRequestSkipsOriginCheck() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/notices");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer access-token");

        // when
        boolean result = interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("쿠키 인증 조회 요청은 Origin 검증 대상이 아니다")
    void safeCookieRequestSkipsOriginCheck() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/notices");
        request.setCookies(new Cookie("access_token", "access-token"));

        // when
        boolean result = interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        // then
        assertThat(result).isTrue();
    }

    private MockHttpServletRequest unsafeCookieRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/notices");
        request.setCookies(new Cookie("access_token", "access-token"));
        return request;
    }
}
