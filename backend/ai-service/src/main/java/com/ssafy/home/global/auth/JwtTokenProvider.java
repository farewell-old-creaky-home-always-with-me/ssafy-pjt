package com.ssafy.home.global.auth;

import com.ssafy.home.global.exception.AuthException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtTokenProvider {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ACCESS_TOKEN_COOKIE = "access_token";

    private final Key key;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        if (!StringUtils.hasText(jwtProperties.secret())) {
            throw new IllegalStateException("jwt.secret must be configured in application-secret.yml or JWT_SECRET.");
        }
        this.key = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authorization) && authorization.startsWith(BEARER_PREFIX)) {
            String token = authorization.substring(BEARER_PREFIX.length());
            if (isUsableToken(token)) {
                return token;
            }
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ACCESS_TOKEN_COOKIE.equals(cookie.getName()) && isUsableToken(cookie.getValue())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private boolean isUsableToken(String token) {
        return StringUtils.hasText(token)
                && !"undefined".equals(token)
                && !"null".equals(token);
    }

    public Long getMemberId(String token) {
        try {
            return Long.valueOf(parseClaims(token).getSubject());
        } catch (NumberFormatException ex) {
            throw new AuthException("인증이 필요합니다.");
        }
    }

    public void validateToken(String token) {
        parseClaims(token);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException ex) {
            throw new AuthException("인증이 필요합니다.");
        }
    }
}
