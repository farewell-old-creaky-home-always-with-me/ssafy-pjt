package com.ssafy.home.global.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class SessionManager {

    public Optional<Long> findCurrentMemberId() {
        HttpSession session = getCurrentSession(false);
        if (session == null) {
            return Optional.empty();
        }

        Object memberId = session.getAttribute(SessionConst.MEMBER_ID);
        if (!(memberId instanceof Long id)) {
            return Optional.empty();
        }

        return Optional.of(id);
    }

    public HttpSession getCurrentSession(boolean create) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }

        return attributes.getRequest().getSession(create);
    }

    public void invalidateCurrentSession() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
