package com.ssafy.home.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.home.global.auth.SessionConst;
import com.ssafy.home.global.auth.SessionManager;
import com.ssafy.home.member.dto.MemberCreateRequest;
import com.ssafy.home.member.dto.MemberDetailResponse;
import com.ssafy.home.member.service.MemberService;
import com.ssafy.home.support.WebMvcTestConfig;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
@Import(WebMvcTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private SessionManager sessionManager;

    @Test
    @DisplayName("유효한 회원가입 요청이면 201을 반환한다")
    void createMemberReturns201() throws Exception {
        // given
        MemberCreateRequest request = createRequest();
        given(memberService.createMember(any(MemberCreateRequest.class))).willReturn(memberResponse());

        // when / then
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value(1L))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    @DisplayName("이메일 형식이 잘못되면 400을 반환한다")
    void createMemberReturns400WhenEmailInvalid() throws Exception {
        // given
        MemberCreateRequest request = new MemberCreateRequest("invalid-email", "password123", "홍길동");

        // when / then
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COMMON_INVALID_INPUT"));
    }

    @Test
    @DisplayName("세션 없이 내 정보 조회를 호출하면 401을 반환한다")
    void getMyMemberReturns401WithoutSession() throws Exception {
        // when / then
        mockMvc.perform(get("/api/members/me").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_UNAUTHORIZED"));
    }

    @Test
    @DisplayName("로그인 세션이 있으면 내 정보를 조회한다")
    void getMyMemberReturns200WithSession() throws Exception {
        // given
        MockHttpSession session = loggedInSession(1L);
        given(memberService.getMyMember(1L)).willReturn(memberResponse());

        // when / then
        mockMvc.perform(get("/api/members/me")
                        .session(session)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(1L))
                .andExpect(jsonPath("$.name").value("홍길동"));
    }

    @Test
    @DisplayName("회원 탈퇴 시 세션을 무효화하고 204를 반환한다")
    void deleteMyMemberInvalidatesSession() throws Exception {
        // given
        MockHttpSession session = loggedInSession(1L);

        // when / then
        mockMvc.perform(delete("/api/members/me").session(session))
                .andExpect(status().isNoContent());

        verify(memberService).deleteMyMember(1L);
        verify(sessionManager).invalidateCurrentSession();
    }

    private MemberCreateRequest createRequest() {
        return new MemberCreateRequest("user@example.com", "password123", "홍길동");
    }

    private MemberDetailResponse memberResponse() {
        return new MemberDetailResponse(1L, "user@example.com", "홍길동", LocalDateTime.of(2026, 6, 1, 10, 0));
    }

    private MockHttpSession loggedInSession(Long memberId) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.MEMBER_ID, memberId);
        session.setAttribute(SessionConst.IS_ADMIN, false);
        return session;
    }
}
