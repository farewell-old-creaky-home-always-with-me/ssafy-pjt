package com.ssafy.home.auth.service;

import com.ssafy.home.auth.dto.AuthMeResponse;
import com.ssafy.home.auth.dto.LoginRequest;
import com.ssafy.home.auth.dto.LoginResponse;
import com.ssafy.home.global.auth.SessionConst;
import com.ssafy.home.global.auth.SessionManager;
import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.exception.ErrorCode;
import com.ssafy.home.member.mapper.MemberMapper;
import com.ssafy.home.member.mapper.dto.MemberDetailResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final SessionManager sessionManager;

    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpServletRequest) {
        MemberDetailResult member = memberMapper.findByEmail(request.email().trim());
        if (member == null || !passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new CustomException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }

        HttpSession existingSession = httpServletRequest.getSession(false);
        if (existingSession != null) {
            existingSession.invalidate();
        }

        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute(SessionConst.MEMBER_ID, member.getId());
        session.setAttribute(SessionConst.IS_ADMIN, member.isAdmin());

        return new LoginResponse(member.getId(), member.getName(), member.isAdmin());
    }

    @Transactional(readOnly = true)
    public AuthMeResponse getAuthMe() {
        return sessionManager.findCurrentMemberId()
                .map(this::toAuthMeResponse)
                .orElseGet(() -> new AuthMeResponse(false, null, null, null));
    }

    private AuthMeResponse toAuthMeResponse(Long memberId) {
        MemberDetailResult member = memberMapper.findById(memberId);
        if (member == null) {
            sessionManager.invalidateCurrentSession();
            return new AuthMeResponse(false, null, null, null);
        }

        return new AuthMeResponse(true, member.getId(), member.getName(), member.isAdmin());
    }
}
