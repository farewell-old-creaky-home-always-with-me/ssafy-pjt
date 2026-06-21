package com.ssafy.home.global.interceptor;

import com.ssafy.home.global.auth.AuthConst;
import com.ssafy.home.global.auth.JwtTokenProvider;
import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        boolean loginRequired = handlerMethod.hasMethodAnnotation(LoginRequired.class)
                || handlerMethod.getBeanType().isAnnotationPresent(LoginRequired.class);
        boolean adminOnly = handlerMethod.hasMethodAnnotation(AdminOnly.class)
                || handlerMethod.getBeanType().isAnnotationPresent(AdminOnly.class);

        if (!loginRequired && !adminOnly) {
            return true;
        }

        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) {
            throw new CustomException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        jwtTokenProvider.validateToken(token);
        Long memberId = jwtTokenProvider.getMemberId(token);
        boolean isAdmin = jwtTokenProvider.isAdmin(token);

        request.setAttribute(AuthConst.MEMBER_ID, memberId);
        request.setAttribute(AuthConst.IS_ADMIN, isAdmin);

        if (adminOnly && !isAdmin) {
            throw new CustomException(ErrorCode.AUTH_FORBIDDEN);
        }

        return true;
    }
}
