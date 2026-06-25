package com.ssafy.home.housinginfo.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ssafy.home.housinginfo.dto.HousingInfoResponse;
import com.ssafy.home.housinginfo.service.HousingInfoService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class HousingInfoControllerTest {

    private HousingInfoService housingInfoService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        housingInfoService = org.mockito.Mockito.mock(HousingInfoService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new HousingInfoController(housingInfoService))
                .setMessageConverters(jsonMessageConverter())
                .build();
    }

    @Test
    @DisplayName("주거 정보 목록을 조회한다")
    void getHousingInfo() throws Exception {
        // given
        given(housingInfoService.getHousingInfo("policy", 10)).willReturn(List.of(housingInfoResponse()));

        // when / then
        mockMvc.perform(get("/api/housing-info")
                        .param("type", "policy")
                        .param("limit", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("청년 전세 지원"))
                .andExpect(jsonPath("$[0].content").value("지원 내용"))
                .andExpect(jsonPath("$[0].sourceName").value("주거복지포털"))
                .andExpect(jsonPath("$[0].sourceUrl").value("https://example.com/info/1"))
                .andExpect(jsonPath("$[0].infoType").value("POLICY"))
                .andExpect(jsonPath("$[0].publishedAt").value("2026-06-25T10:00:00"));
    }

    private HousingInfoResponse housingInfoResponse() {
        return new HousingInfoResponse(
                1L,
                "청년 전세 지원",
                "지원 내용",
                "주거복지포털",
                "https://example.com/info/1",
                "POLICY",
                LocalDateTime.of(2026, 6, 25, 10, 0)
        );
    }

    private MappingJackson2HttpMessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }
}
