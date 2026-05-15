package com.ssafy.home.global.interceptor;

import com.ssafy.home.global.exception.ForbiddenException;
import com.ssafy.home.global.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String SESSION_MEMBER_ID = "memberId";
    public static final String SESSION_IS_ADMIN = "isAdmin";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        boolean loginRequired = handlerMethod.hasMethodAnnotation(LoginRequired.class)
                || handlerMethod.getBeanType().isAnnotationPresent(LoginRequired.class);
        boolean adminOnly = handlerMethod.hasMethodAnnotation(AdminOnly.class)
                || handlerMethod.getBeanType().isAnnotationPresent(AdminOnly.class);

        Object memberId = request.getSession(false) == null
                ? null
                : request.getSession(false).getAttribute(SESSION_MEMBER_ID);

        if ((loginRequired || adminOnly) && memberId == null) {
            throw new UnauthorizedException("AUTH_UNAUTHORIZED", "로그인이 필요합니다");
        }

        if (adminOnly) {
            Object isAdmin = request.getSession(false).getAttribute(SESSION_IS_ADMIN);
            if (!(isAdmin instanceof Boolean admin) || !admin) {
                throw new ForbiddenException("AUTH_FORBIDDEN", "접근 권한이 없습니다");
            }
        }

        return true;
    }
}
