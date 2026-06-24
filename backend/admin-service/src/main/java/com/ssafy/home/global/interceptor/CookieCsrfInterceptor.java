package com.ssafy.home.global.interceptor;

import static com.ssafy.home.global.exception.ErrorCode.AUTH_FORBIDDEN;

import com.ssafy.home.global.config.AdminCsrfProperties;
import com.ssafy.home.global.exception.CustomException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CookieCsrfInterceptor implements HandlerInterceptor {

    private static final String ACCESS_TOKEN_COOKIE = "access_token";

    private final Set<String> allowedOrigins;

    public CookieCsrfInterceptor(AdminCsrfProperties properties) {
        this.allowedOrigins = properties.allowedOrigins()
                .stream()
                .map(this::normalizeOrigin)
                .filter(StringUtils::hasText)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (isSafeMethod(request.getMethod()) || !hasAccessTokenCookie(request)) {
            return true;
        }

        if (isAllowedOrigin(request.getHeader(HttpHeaders.ORIGIN))
                || isAllowedOrigin(extractOrigin(request.getHeader(HttpHeaders.REFERER)))) {
            return true;
        }

        throw new CustomException(AUTH_FORBIDDEN);
    }

    private boolean isSafeMethod(String method) {
        return HttpMethod.GET.matches(method)
                || HttpMethod.HEAD.matches(method)
                || HttpMethod.OPTIONS.matches(method)
                || HttpMethod.TRACE.matches(method);
    }

    private boolean hasAccessTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }

        for (Cookie cookie : cookies) {
            if (ACCESS_TOKEN_COOKIE.equals(cookie.getName()) && StringUtils.hasText(cookie.getValue())) {
                return true;
            }
        }
        return false;
    }

    private boolean isAllowedOrigin(String origin) {
        String normalizedOrigin = normalizeOrigin(origin);
        return StringUtils.hasText(normalizedOrigin) && allowedOrigins.contains(normalizedOrigin);
    }

    private String normalizeOrigin(String origin) {
        if (!StringUtils.hasText(origin)) {
            return null;
        }

        try {
            URI uri = new URI(origin.trim());
            if (!StringUtils.hasText(uri.getScheme()) || !StringUtils.hasText(uri.getHost())) {
                return null;
            }
            return uri.getPort() == -1
                    ? "%s://%s".formatted(uri.getScheme(), uri.getHost())
                    : "%s://%s:%d".formatted(uri.getScheme(), uri.getHost(), uri.getPort());
        } catch (URISyntaxException ex) {
            return null;
        }
    }

    private String extractOrigin(String referer) {
        return normalizeOrigin(referer);
    }
}
