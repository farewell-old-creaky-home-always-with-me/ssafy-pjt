package com.ssafy.home.member.controller;

import com.ssafy.home.global.interceptor.LoginRequired;
import com.ssafy.home.member.dto.CreateMemberRequest;
import com.ssafy.home.member.dto.MemberResponse;
import com.ssafy.home.member.dto.MemberUpdateResponse;
import com.ssafy.home.member.dto.UpdateMemberRequest;
import com.ssafy.home.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
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
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody CreateMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(memberService.createMember(request));
    }

    @LoginRequired
    @GetMapping("/me")
    public MemberResponse getMyMember(HttpSession session) {
        return memberService.getMyMember(getMemberId(session));
    }

    @LoginRequired
    @PutMapping("/me")
    public MemberUpdateResponse updateMyMember(
            @Valid @RequestBody UpdateMemberRequest request,
            HttpSession session
    ) {
        return memberService.updateMyMember(getMemberId(session), request);
    }

    @LoginRequired
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyMember(HttpSession session) {
        memberService.deleteMyMember(getMemberId(session));
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    private Long getMemberId(HttpSession session) {
        return (Long) session.getAttribute("memberId");
    }
}
