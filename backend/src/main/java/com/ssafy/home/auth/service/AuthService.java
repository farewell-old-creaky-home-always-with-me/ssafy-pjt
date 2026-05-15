package com.ssafy.home.auth.service;

import com.ssafy.home.auth.dto.AuthMeResponse;
import com.ssafy.home.auth.dto.LoginRequest;
import com.ssafy.home.auth.dto.LoginResponse;
import com.ssafy.home.global.exception.UnauthorizedException;
import com.ssafy.home.global.interceptor.AuthInterceptor;
import com.ssafy.home.member.dto.MemberEntity;
import com.ssafy.home.member.mapper.MemberMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthService(MemberMapper memberMapper, PasswordEncoder passwordEncoder) {
        this.memberMapper = memberMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request, HttpServletRequest httpServletRequest) {
        MemberEntity member = memberMapper.findByEmail(request.email().trim());
        if (member == null || !passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new UnauthorizedException("AUTH_INVALID_CREDENTIALS", "이메일 또는 비밀번호가 올바르지 않습니다");
        }

        HttpSession existingSession = httpServletRequest.getSession(false);
        if (existingSession != null) {
            existingSession.invalidate();
        }

        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute(AuthInterceptor.SESSION_MEMBER_ID, member.getMemberId());
        session.setAttribute(AuthInterceptor.SESSION_IS_ADMIN, member.isAdmin());

        return new LoginResponse(member.getMemberId(), member.getName(), member.isAdmin());
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public AuthMeResponse getAuthMe(HttpSession session) {
        if (session == null) {
            return new AuthMeResponse(false, null, null, null);
        }

        Object memberId = session.getAttribute(AuthInterceptor.SESSION_MEMBER_ID);
        if (!(memberId instanceof Long id)) {
            return new AuthMeResponse(false, null, null, null);
        }

        MemberEntity member = memberMapper.findById(id);
        if (member == null) {
            session.invalidate();
            return new AuthMeResponse(false, null, null, null);
        }

        return new AuthMeResponse(true, member.getMemberId(), member.getName(), member.isAdmin());
    }
}
