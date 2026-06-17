package com.ssafy.home.member.service;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import com.ssafy.home.member.dto.CreateMemberRequest;
import com.ssafy.home.member.dto.MemberResponse;
import com.ssafy.home.member.dto.MemberUpdateResponse;
import com.ssafy.home.member.dto.UpdateMemberRequest;
import com.ssafy.home.member.mapper.MemberMapper;
import com.ssafy.home.member.mapper.dto.MemberCreateParam;
import com.ssafy.home.member.mapper.dto.MemberDetailResult;
import com.ssafy.home.member.mapper.dto.MemberUpdateParam;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponse createMember(CreateMemberRequest request) {
        if (memberMapper.existsByEmail(request.email())) {
            throw new CustomException(ErrorCode.MEMBER_DUPLICATE_EMAIL);
        }

        MemberCreateParam param = new MemberCreateParam();
        param.setEmail(request.email().trim());
        param.setPassword(passwordEncoder.encode(request.password()));
        param.setName(request.name().trim());
        param.setAdmin(false);

        memberMapper.insert(param);
        MemberDetailResult savedMember = memberMapper.findById(param.getId());
        return toMemberResponse(requireMember(savedMember, param.getId()));
    }

    @Transactional(readOnly = true)
    public MemberResponse getMyMember(Long memberId) {
        return toMemberResponse(requireMember(memberMapper.findById(memberId), memberId));
    }

    @Transactional
    public MemberUpdateResponse updateMyMember(Long memberId, UpdateMemberRequest request) {
        MemberDetailResult member = requireMember(memberMapper.findById(memberId), memberId);

        MemberUpdateParam param = new MemberUpdateParam();
        param.setId(memberId);
        param.setName(request.name().trim());
        param.setPassword(passwordEncoder.encode(request.password()));
        memberMapper.update(param);

        return new MemberUpdateResponse(memberId, param.getName());
    }

    @Transactional
    public void deleteMyMember(Long memberId) {
        requireMember(memberMapper.findById(memberId), memberId);
        memberMapper.delete(memberId);
    }

    @Transactional(readOnly = true)
    public MemberDetailResult getMemberDetail(Long memberId) {
        return requireMember(memberMapper.findById(memberId), memberId);
    }

    private MemberDetailResult requireMember(MemberDetailResult member, Long memberId) {
        if (member == null) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }
        return member;
    }

    private MemberResponse toMemberResponse(MemberDetailResult member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getCreatedAt()
        );
    }
}
