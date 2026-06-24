package com.ssafy.home.member.service;

import static com.ssafy.home.global.exception.ErrorCode.MEMBER_DUPLICATE_EMAIL;
import static com.ssafy.home.global.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.member.dto.MemberCreateRequest;
import com.ssafy.home.member.dto.MemberDetailResponse;
import com.ssafy.home.member.dto.MemberPasswordResetRequest;
import com.ssafy.home.member.dto.MemberUpdateRequest;
import com.ssafy.home.member.dto.MemberUpdateResponse;
import com.ssafy.home.member.mapper.MemberMapper;
import com.ssafy.home.member.mapper.dto.MemberCreateParam;
import com.ssafy.home.member.mapper.dto.MemberDetailResult;
import com.ssafy.home.member.mapper.dto.MemberUpdateParam;
import com.ssafy.home.member.service.mail.PasswordResetMailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final TemporaryPasswordGenerator temporaryPasswordGenerator;
    private final PasswordResetMailSender passwordResetMailSender;

    @Transactional
    public MemberDetailResponse createMember(MemberCreateRequest request) {
        if (memberMapper.existsByEmail(request.email())) {
            throw new CustomException(MEMBER_DUPLICATE_EMAIL);
        }

        MemberCreateParam param = new MemberCreateParam();
        param.setEmail(request.email().trim());
        param.setPassword(passwordEncoder.encode(request.password()));
        param.setName(request.name().trim());
        param.setPhone(PhoneNumberNormalizer.normalize(request.phone()));
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
        param.setPhone(PhoneNumberNormalizer.normalize(request.phone()));
        memberMapper.updateById(param);

        return MemberUpdateResponse.of(memberId, param.getName());
    }

    @Transactional
    public void resetPassword(MemberPasswordResetRequest request) {
        MemberDetailResult member = memberMapper.findByNameAndEmailAndPhone(
                request.name().trim(),
                request.email().trim(),
                PhoneNumberNormalizer.normalize(request.phone())
        );
        if (member == null) {
            log.info(
                    "Password reset requested for non-matching member identity: email={}",
                    request.email().trim()
            );
            return;
        }

        String temporaryPassword = temporaryPasswordGenerator.generate();
        String encodedPassword = passwordEncoder.encode(temporaryPassword);
        int updatedRows = memberMapper.updatePasswordById(member.getId(), encodedPassword);
        if (updatedRows != 1) {
            log.warn("Password reset skipped because password update affected {} rows: memberId={}", updatedRows, member.getId());
            return;
        }

        sendPasswordResetMailAfterCommit(member.getEmail(), member.getName(), temporaryPassword);
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

    private void sendPasswordResetMailAfterCommit(String email, String name, String temporaryPassword) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            passwordResetMailSender.send(email, name, temporaryPassword);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                passwordResetMailSender.send(email, name, temporaryPassword);
            }
        });
    }
}
