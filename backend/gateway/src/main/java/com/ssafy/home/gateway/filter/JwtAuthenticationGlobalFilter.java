package com.ssafy.home.gateway.filter;

import com.ssafy.home.gateway.auth.JwtTokenValidator;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationGlobalFilter implements GlobalFilter, Ordered {

    private static final String API_PATH_PREFIX = "/api/";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ACCESS_TOKEN_COOKIE = "access_token";
    private static final PublicEndpoint[] PUBLIC_ENDPOINTS = {
            PublicEndpoint.exact(HttpMethod.POST, "/api/auth/login"),
            PublicEndpoint.exact(HttpMethod.POST, "/api/auth/logout"),
            PublicEndpoint.exact(HttpMethod.GET, "/api/auth/me"),
            PublicEndpoint.exact(HttpMethod.POST, "/api/members"),
            PublicEndpoint.exact(HttpMethod.POST, "/api/members/password-reset"),
            PublicEndpoint.exact(HttpMethod.GET, "/api/notices"),
            PublicEndpoint.prefix(HttpMethod.GET, "/api/notices/"),
            PublicEndpoint.exact(HttpMethod.GET, "/api/houses"),
            PublicEndpoint.prefix(HttpMethod.GET, "/api/houses/"),
            PublicEndpoint.exact(HttpMethod.GET, "/api/commercial"),
            PublicEndpoint.exact(HttpMethod.GET, "/api/environment"),
            PublicEndpoint.exact(HttpMethod.GET, "/api/stats")
    };

    private final JwtTokenValidator jwtTokenValidator;

    public JwtAuthenticationGlobalFilter(JwtTokenValidator jwtTokenValidator) {
        this.jwtTokenValidator = jwtTokenValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!requiresJwtValidation(exchange)) {
            return chain.filter(exchange);
        }

        String token = resolveToken(exchange);
        if (token == null || !jwtTokenValidator.isValid(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private boolean requiresJwtValidation(ServerWebExchange exchange) {
        if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
            return false;
        }
        if (isPublicEndpoint(exchange.getRequest())) {
            return false;
        }
        return exchange.getRequest().getURI().getPath().startsWith(API_PATH_PREFIX);
    }

    private boolean isPublicEndpoint(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        for (PublicEndpoint endpoint : PUBLIC_ENDPOINTS) {
            if (endpoint.matches(method, path)) {
                return true;
            }
        }

        return false;
    }

    private String resolveToken(ServerWebExchange exchange) {
        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authorization) && authorization.startsWith(BEARER_PREFIX)) {
            String bearerToken = authorization.substring(BEARER_PREFIX.length());
            if (isUsableToken(bearerToken)) {
                return bearerToken;
            }
        }
        return exchange.getRequest().getCookies().getFirst(ACCESS_TOKEN_COOKIE) == null
                ? null
                : exchange.getRequest().getCookies().getFirst(ACCESS_TOKEN_COOKIE).getValue();
    }

    private boolean isUsableToken(String token) {
        return StringUtils.hasText(token)
                && !"undefined".equals(token)
                && !"null".equals(token);
    }

    private record PublicEndpoint(HttpMethod method, String path, boolean prefix) {

        private static PublicEndpoint exact(HttpMethod method, String path) {
            return new PublicEndpoint(method, path, false);
        }

        private static PublicEndpoint prefix(HttpMethod method, String path) {
            return new PublicEndpoint(method, path, true);
        }

        private boolean matches(HttpMethod requestMethod, String requestPath) {
            if (!method.equals(requestMethod)) {
                return false;
            }
            if (prefix) {
                return requestPath.startsWith(path);
            }
            return path.equals(requestPath);
        }
    }
}
