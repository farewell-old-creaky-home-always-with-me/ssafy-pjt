package com.ssafy.home.admin.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ssafy.home.admin.dto.RegionCodeCollectResponse;
import com.ssafy.home.admin.service.BatchJobService;
import com.ssafy.home.global.auth.SessionConst;
import com.ssafy.home.support.WebMvcTestConfig;
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

@WebMvcTest(AdminBatchController.class)
@Import(WebMvcTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminBatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BatchJobService batchJobService;

    @Test
    @DisplayName("법정동 수집 배치를 실행하면 200을 반환한다")
    void collectRegionCodesReturns200() throws Exception {
        // given
        given(batchJobService.collectRegionCodes(1L)).willReturn(
                new RegionCodeCollectResponse(10L, "regionCodeCollectJob", "STARTED")
        );

        // when / then
        mockMvc.perform(post("/api/admin/batch/region-codes")
                        .session(adminSession(1L))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobExecutionId").value(10))
                .andExpect(jsonPath("$.jobName").value("regionCodeCollectJob"))
                .andExpect(jsonPath("$.status").value("STARTED"));
    }

    private MockHttpSession adminSession(Long memberId) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.MEMBER_ID, memberId);
        session.setAttribute(SessionConst.IS_ADMIN, true);
        return session;
    }
}
