package com.ssafy.home.global.auth;

import com.ssafy.home.global.exception.CustomException;
import static com.ssafy.home.global.exception.ErrorCode.AUTH_UNAUTHORIZED;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginMemberIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMemberId.class)
                && Long.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new CustomException(AUTH_UNAUTHORIZED);
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new CustomException(AUTH_UNAUTHORIZED);
        }

        Object memberId = session.getAttribute(SessionConst.MEMBER_ID);
        if (!(memberId instanceof Long id)) {
            throw new CustomException(AUTH_UNAUTHORIZED);
        }

        return id;
    }
}
