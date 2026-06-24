package com.ssafy.home.gateway.filter;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.gateway.auth.JwtProperties;
import com.ssafy.home.gateway.auth.JwtTokenValidator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

class JwtAuthenticationGlobalFilterTest {

    private static final String SECRET = "test-jwt-secret-key-for-ssafy-home-project-2026";

    private JwtAuthenticationGlobalFilter filter;

    @BeforeEach
    void setUp() {
        JwtTokenValidator jwtTokenValidator = new JwtTokenValidator(new JwtProperties(SECRET));
        filter = new JwtAuthenticationGlobalFilter(jwtTokenValidator);
    }

    @Test
    @DisplayName("API 요청에 토큰이 없으면 401을 반환한다")
    void filterReturns401WhenTokenMissing() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/members/me")
        );
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        // when
        filter.filter(exchange, chain(chainCalled)).block();

        // then
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(chainCalled).isFalse();
    }

    @Test
    @DisplayName("API 요청의 토큰이 잘못되면 401을 반환한다")
    void filterReturns401WhenTokenInvalid() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/members/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token")
        );
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        // when
        filter.filter(exchange, chain(chainCalled)).block();

        // then
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(chainCalled).isFalse();
    }

    @Test
    @DisplayName("API 요청의 토큰이 유효하면 다음 필터를 호출한다")
    void filterCallsChainWhenTokenValid() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/houses")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken())
        );
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        // when
        filter.filter(exchange, chain(chainCalled)).block();

        // then
        assertThat(exchange.getResponse().getStatusCode()).isNull();
        assertThat(chainCalled).isTrue();
    }

    @Test
    @DisplayName("OPTIONS 요청은 토큰 없이 다음 필터를 호출한다")
    void filterCallsChainForOptionsRequest() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.options("/api/houses")
        );
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        // when
        filter.filter(exchange, chain(chainCalled)).block();

        // then
        assertThat(exchange.getResponse().getStatusCode()).isNull();
        assertThat(chainCalled).isTrue();
    }

    @Test
    @DisplayName("로그인 요청은 토큰 없이 다음 필터를 호출한다")
    void filterCallsChainForLoginRequest() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/api/auth/login")
        );
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        // when
        filter.filter(exchange, chain(chainCalled)).block();

        // then
        assertThat(exchange.getResponse().getStatusCode()).isNull();
        assertThat(chainCalled).isTrue();
    }

    @Test
    @DisplayName("회원가입 요청은 토큰 없이 다음 필터를 호출한다")
    void filterCallsChainForMemberCreateRequest() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/api/members")
        );
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        // when
        filter.filter(exchange, chain(chainCalled)).block();

        // then
        assertThat(exchange.getResponse().getStatusCode()).isNull();
        assertThat(chainCalled).isTrue();
    }

    @Test
    @DisplayName("비밀번호 재설정 요청은 토큰 없이 다음 필터를 호출한다")
    void filterCallsChainForPasswordResetRequest() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/api/members/password-reset")
        );
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        // when
        filter.filter(exchange, chain(chainCalled)).block();

        // then
        assertThat(exchange.getResponse().getStatusCode()).isNull();
        assertThat(chainCalled).isTrue();
    }

    @Test
    @DisplayName("공지사항 조회 요청은 토큰 없이 다음 필터를 호출한다")
    void filterCallsChainForNoticeReadRequest() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/notices")
        );
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        // when
        filter.filter(exchange, chain(chainCalled)).block();

        // then
        assertThat(exchange.getResponse().getStatusCode()).isNull();
        assertThat(chainCalled).isTrue();
    }

    @Test
    @DisplayName("공지사항 상세 조회 요청은 토큰 없이 다음 필터를 호출한다")
    void filterCallsChainForNoticeDetailReadRequest() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/notices/1")
        );
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        // when
        filter.filter(exchange, chain(chainCalled)).block();

        // then
        assertThat(exchange.getResponse().getStatusCode()).isNull();
        assertThat(chainCalled).isTrue();
    }

    @Test
    @DisplayName("공지사항 작성 요청은 토큰이 없으면 401을 반환한다")
    void filterReturns401ForNoticeWriteRequestWithoutToken() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/api/notices")
        );
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        // when
        filter.filter(exchange, chain(chainCalled)).block();

        // then
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(chainCalled).isFalse();
    }

    @Test
    @DisplayName("관리자 API 요청은 토큰이 없으면 401을 반환한다")
    void filterReturns401ForAdminRequestWithoutToken() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/api/admin/batch/region-codes")
        );
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        // when
        filter.filter(exchange, chain(chainCalled)).block();

        // then
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(chainCalled).isFalse();
    }

    private GatewayFilterChain chain(AtomicBoolean chainCalled) {
        return exchange -> {
            chainCalled.set(true);
            return Mono.empty();
        };
    }

    private String validToken() {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject("1")
                .claim("isAdmin", false)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(3600)))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
}
