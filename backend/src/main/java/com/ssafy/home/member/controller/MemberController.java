package com.ssafy.home.member.controller;

import com.ssafy.home.global.auth.LoginMemberId;
import com.ssafy.home.global.auth.SessionManager;
import com.ssafy.home.global.interceptor.LoginRequired;
import com.ssafy.home.member.dto.CreateMemberRequest;
import com.ssafy.home.member.dto.MemberResponse;
import com.ssafy.home.member.dto.MemberUpdateResponse;
import com.ssafy.home.member.dto.UpdateMemberRequest;
import com.ssafy.home.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController implements MemberApiDocs {

    private final MemberService memberService;
    private final SessionManager sessionManager;

    @PostMapping
    @Override
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody CreateMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(memberService.createMember(request));
    }

    @LoginRequired
    @GetMapping("/me")
    @Override
    public MemberResponse getMyMember(@LoginMemberId Long memberId) {
        return memberService.getMyMember(memberId);
    }

    @LoginRequired
    @PutMapping("/me")
    @Override
    public MemberUpdateResponse updateMyMember(
            @Valid @RequestBody UpdateMemberRequest request,
            @LoginMemberId Long memberId
    ) {
        return memberService.updateMyMember(memberId, request);
    }

    @LoginRequired
    @DeleteMapping("/me")
    @Override
    public ResponseEntity<Void> deleteMyMember(@LoginMemberId Long memberId) {
        memberService.deleteMyMember(memberId);
        sessionManager.invalidateCurrentSession();
        return ResponseEntity.noContent().build();
    }
}
