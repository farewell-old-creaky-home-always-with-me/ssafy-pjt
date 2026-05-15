package com.ssafy.home.member.service;

import com.ssafy.home.global.exception.DuplicateResourceException;
import com.ssafy.home.global.exception.ResourceNotFoundException;
import com.ssafy.home.member.dto.CreateMemberRequest;
import com.ssafy.home.member.dto.MemberEntity;
import com.ssafy.home.member.dto.MemberResponse;
import com.ssafy.home.member.dto.MemberUpdateResponse;
import com.ssafy.home.member.dto.UpdateMemberRequest;
import com.ssafy.home.member.mapper.MemberMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberMapper memberMapper, PasswordEncoder passwordEncoder) {
        this.memberMapper = memberMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public MemberResponse createMember(CreateMemberRequest request) {
        if (memberMapper.existsByEmail(request.email())) {
            throw new DuplicateResourceException("MEMBER_DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다");
        }

        MemberEntity member = new MemberEntity();
        member.setEmail(request.email().trim());
        member.setPassword(passwordEncoder.encode(request.password()));
        member.setName(request.name().trim());
        member.setAdmin(false);

        memberMapper.insertMember(member);
        MemberEntity savedMember = memberMapper.findById(member.getMemberId());
        return toMemberResponse(requireMember(savedMember, member.getMemberId()));
    }

    public MemberResponse getMyMember(Long memberId) {
        return toMemberResponse(requireMember(memberMapper.findById(memberId), memberId));
    }

    public MemberUpdateResponse updateMyMember(Long memberId, UpdateMemberRequest request) {
        MemberEntity member = requireMember(memberMapper.findById(memberId), memberId);
        member.setName(request.name().trim());
        member.setPassword(passwordEncoder.encode(request.password()));
        memberMapper.updateMember(member);
        return new MemberUpdateResponse(memberId, member.getName());
    }

    public void deleteMyMember(Long memberId) {
        requireMember(memberMapper.findById(memberId), memberId);
        memberMapper.deleteMember(memberId);
    }

    public MemberEntity getMemberEntity(Long memberId) {
        return requireMember(memberMapper.findById(memberId), memberId);
    }

    private MemberEntity requireMember(MemberEntity member, Long memberId) {
        if (member == null) {
            throw new ResourceNotFoundException("MEMBER_NOT_FOUND", "해당 회원을 찾을 수 없습니다");
        }
        return member;
    }

    private MemberResponse toMemberResponse(MemberEntity member) {
        return new MemberResponse(
                member.getMemberId(),
                member.getEmail(),
                member.getName(),
                member.getCreatedAt()
        );
    }
}
