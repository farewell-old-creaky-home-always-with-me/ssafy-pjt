package com.ssafy.home.global.interceptor;

import com.ssafy.home.global.auth.SessionConst;
import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String SESSION_MEMBER_ID = SessionConst.MEMBER_ID;
    public static final String SESSION_IS_ADMIN = SessionConst.IS_ADMIN;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        boolean loginRequired = handlerMethod.hasMethodAnnotation(LoginRequired.class)
                || handlerMethod.getBeanType().isAnnotationPresent(LoginRequired.class);
        boolean adminOnly = handlerMethod.hasMethodAnnotation(AdminOnly.class)
                || handlerMethod.getBeanType().isAnnotationPresent(AdminOnly.class);

        HttpSession session = request.getSession(false);
        Object memberId = session == null
                ? null
                : session.getAttribute(SESSION_MEMBER_ID);

        if ((loginRequired || adminOnly) && memberId == null) {
            throw new CustomException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        if (adminOnly) {
            Object isAdmin = session.getAttribute(SESSION_IS_ADMIN);
            if (!(isAdmin instanceof Boolean admin) || !admin) {
                throw new CustomException(ErrorCode.AUTH_FORBIDDEN);
            }
        }

        return true;
    }
}
