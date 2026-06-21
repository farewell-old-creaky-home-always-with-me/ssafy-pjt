package com.ssafy.home.auth.service;

import com.ssafy.home.auth.dto.AuthMeResponse;
import com.ssafy.home.auth.dto.LoginRequest;
import com.ssafy.home.auth.dto.LoginResponse;
import com.ssafy.home.global.auth.JwtTokenProvider;
import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.exception.ErrorCode;
import com.ssafy.home.member.dto.MemberEntity;
import com.ssafy.home.member.mapper.MemberMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest request) {
        MemberEntity member = memberMapper.findByEmail(request.email().trim());
        if (member == null || !passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new CustomException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), member.isAdmin());
        return new LoginResponse(member.getId(), member.getName(), member.isAdmin(), accessToken);
    }

    public AuthMeResponse getAuthMe(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) {
            return new AuthMeResponse(false, null, null, null);
        }

        Long memberId;
        try {
            jwtTokenProvider.validateToken(token);
            memberId = jwtTokenProvider.getMemberId(token);
        } catch (CustomException ex) {
            return new AuthMeResponse(false, null, null, null);
        }

        MemberEntity member = memberMapper.findById(memberId);
        if (member == null) {
            return new AuthMeResponse(false, null, null, null);
        }

        return new AuthMeResponse(true, member.getId(), member.getName(), member.isAdmin());
    }
}
