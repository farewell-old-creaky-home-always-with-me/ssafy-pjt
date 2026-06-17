package com.ssafy.home.member.service;

import static com.ssafy.home.global.exception.ErrorCode.MEMBER_DUPLICATE_EMAIL;
import static com.ssafy.home.global.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.member.dto.MemberCreateRequest;
import com.ssafy.home.member.dto.MemberDetailResponse;
import com.ssafy.home.member.dto.MemberUpdateRequest;
import com.ssafy.home.member.dto.MemberUpdateResponse;
import com.ssafy.home.member.mapper.MemberMapper;
import com.ssafy.home.member.mapper.dto.MemberCreateParam;
import com.ssafy.home.member.mapper.dto.MemberDetailResult;
import com.ssafy.home.member.mapper.dto.MemberUpdateParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberDetailResponse createMember(MemberCreateRequest request) {
        if (memberMapper.existsByEmail(request.email())) {
            throw new CustomException(MEMBER_DUPLICATE_EMAIL);
        }

        MemberCreateParam param = new MemberCreateParam();
        param.setEmail(request.email().trim());
        param.setPassword(passwordEncoder.encode(request.password()));
        param.setName(request.name().trim());
        param.setAdmin(false);

        memberMapper.insert(param);
        MemberDetailResult savedMember = memberMapper.findById(param.getId());
        return MemberDetailResponse.from(requireMember(savedMember, param.getId()));
    }

    @Transactional(readOnly = true)
    public MemberDetailResponse getMyMember(Long memberId) {
        return MemberDetailResponse.from(requireMember(memberMapper.findById(memberId), memberId));
    }

    @Transactional
    public MemberUpdateResponse updateMyMember(Long memberId, MemberUpdateRequest request) {
        requireMember(memberMapper.findById(memberId), memberId);

        MemberUpdateParam param = new MemberUpdateParam();
        param.setId(memberId);
        param.setName(request.name().trim());
        param.setPassword(passwordEncoder.encode(request.password()));
        memberMapper.updateById(param);

        return MemberUpdateResponse.of(memberId, param.getName());
    }

    @Transactional
    public void deleteMyMember(Long memberId) {
        requireMember(memberMapper.findById(memberId), memberId);
        memberMapper.deleteById(memberId);
    }

    @Transactional(readOnly = true)
    public MemberDetailResult getMemberDetail(Long memberId) {
        return requireMember(memberMapper.findById(memberId), memberId);
    }

    private MemberDetailResult requireMember(MemberDetailResult member, Long memberId) {
        if (member == null) {
            throw new CustomException(MEMBER_NOT_FOUND);
        }
        return member;
    }
}
