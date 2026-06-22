package com.ssafy.home.auth.service;

import static com.ssafy.home.global.exception.ErrorCode.AUTH_INVALID_CREDENTIALS;

import com.ssafy.home.auth.dto.AuthLoginRequest;
import com.ssafy.home.auth.dto.AuthMeResponse;
import com.ssafy.home.auth.dto.LoginResponse;
import com.ssafy.home.global.auth.JwtTokenProvider;
import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.member.mapper.MemberMapper;
import com.ssafy.home.member.mapper.dto.MemberDetailResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse login(AuthLoginRequest request) {
        MemberDetailResult member = memberMapper.findByEmail(request.email().trim());
        if (member == null || !passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new CustomException(AUTH_INVALID_CREDENTIALS);
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), member.isAdmin());
        return new LoginResponse(member.getId(), member.getName(), member.isAdmin(), accessToken);
    }

    @Transactional(readOnly = true)
    public AuthMeResponse getAuthMe(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) {
            return AuthMeResponse.guest();
        }

        Long memberId;
        try {
            jwtTokenProvider.validateToken(token);
            memberId = jwtTokenProvider.getMemberId(token);
        } catch (CustomException ex) {
            return AuthMeResponse.guest();
        }

        MemberDetailResult member = memberMapper.findById(memberId);
        if (member == null) {
            return AuthMeResponse.guest();
        }

        return AuthMeResponse.from(member);
    }
}
