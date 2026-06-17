package com.ssafy.home.auth.service;

import static com.ssafy.home.global.exception.ErrorCode.AUTH_INVALID_CREDENTIALS;

import com.ssafy.home.auth.dto.AuthLoginRequest;
import com.ssafy.home.auth.dto.AuthMeResponse;
import com.ssafy.home.auth.dto.LoginResponse;
import com.ssafy.home.global.auth.SessionConst;
import com.ssafy.home.global.auth.SessionManager;
import com.ssafy.home.global.exception.CustomException;
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
    public LoginResponse login(AuthLoginRequest request, HttpServletRequest httpServletRequest) {
        MemberDetailResult member = memberMapper.findByEmail(request.email().trim());
        if (member == null || !passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new CustomException(AUTH_INVALID_CREDENTIALS);
        }

        HttpSession existingSession = httpServletRequest.getSession(false);
        if (existingSession != null) {
            existingSession.invalidate();
        }

        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute(SessionConst.MEMBER_ID, member.getId());
        session.setAttribute(SessionConst.IS_ADMIN, member.isAdmin());

        return LoginResponse.from(member);
    }

    @Transactional(readOnly = true)
    public AuthMeResponse getAuthMe() {
        return sessionManager.findCurrentMemberId()
                .map(this::toAuthMeResponse)
                .orElseGet(AuthMeResponse::guest);
    }

    private AuthMeResponse toAuthMeResponse(Long memberId) {
        MemberDetailResult member = memberMapper.findById(memberId);
        if (member == null) {
            sessionManager.invalidateCurrentSession();
            return AuthMeResponse.guest();
        }

        return AuthMeResponse.from(member);
    }
}
