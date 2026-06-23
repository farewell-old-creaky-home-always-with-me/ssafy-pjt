package com.ssafy.home.global.auth;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtTokenProvider {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String IS_ADMIN_CLAIM = "isAdmin";

    private final Key key;
    private final long accessTokenExpirationMillis;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        if (!StringUtils.hasText(jwtProperties.secret())) {
            throw new IllegalStateException("jwt.secret must be configured in application-secret.yml or JWT_SECRET.");
        }
        if (jwtProperties.accessTokenExpirationMillis() <= 0) {
            throw new IllegalStateException("jwt.access-token-expiration-millis must be greater than 0.");
        }

        this.key = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMillis = jwtProperties.accessTokenExpirationMillis();
    }

    public String createAccessToken(Long memberId, boolean isAdmin) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(accessTokenExpirationMillis);

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .claim(IS_ADMIN_CLAIM, isAdmin)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(key)
                .compact();
    }

    public String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
            return null;
        }

        return authorization.substring(BEARER_PREFIX.length());
    }

    public Long getMemberId(String token) {
        try {
            return Long.valueOf(parseClaims(token).getSubject());
        } catch (NumberFormatException ex) {
            throw new CustomException(ErrorCode.AUTH_UNAUTHORIZED);
        }
    }

    public boolean isAdmin(String token) {
        Object value = parseClaims(token).get(IS_ADMIN_CLAIM);
        return value instanceof Boolean admin && admin;
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
            throw new CustomException(ErrorCode.AUTH_UNAUTHORIZED);
        }
    }
}
