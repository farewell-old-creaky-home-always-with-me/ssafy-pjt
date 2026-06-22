package com.ssafy.home.house.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ssafy.home.global.auth.JwtTokenProvider;
import com.ssafy.home.global.response.PageResponse;
import com.ssafy.home.house.dto.HouseDetailResponse;
import com.ssafy.home.house.dto.HouseSummaryResponse;
import com.ssafy.home.house.service.HouseService;
import com.ssafy.home.support.WebMvcTestConfig;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HouseController.class)
@Import(WebMvcTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class HouseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HouseService houseService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("지역 코드로 주택 목록을 조회한다")
    void searchHousesReturns200() throws Exception {
        // given
        given(houseService.searchHouses("1168010100", null, null, null, null, 1, 20))
                .willReturn(PageResponse.of(List.of(houseSummaryResponse()), 1L, 1, 20));

        // when / then
        mockMvc.perform(get("/api/houses")
                        .param("regionCode", "1168010100")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.items[0].houseId").value(1L))
                .andExpect(jsonPath("$.items[0].aptName").value("역삼래미안"));
    }

    @Test
    @DisplayName("지역 코드가 없으면 400을 반환한다")
    void searchHousesReturns400WithoutRegionCode() throws Exception {
        // when / then
        mockMvc.perform(get("/api/houses").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COMMON_INVALID_INPUT"));
    }

    @Test
    @DisplayName("주택 상세를 조회한다")
    void getHouseDetailReturns200() throws Exception {
        // given
        given(houseService.getHouseDetail(1L)).willReturn(houseDetailResponse());

        // when / then
        mockMvc.perform(get("/api/houses/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.houseId").value(1L))
                .andExpect(jsonPath("$.aptName").value("역삼래미안"))
                .andExpect(jsonPath("$.deals[0].dealType").value("매매"));
    }

    private HouseSummaryResponse houseSummaryResponse() {
        return new HouseSummaryResponse(
                1L,
                "역삼래미안",
                "757",
                2005,
                "아파트",
                new HouseSummaryResponse.LatestDealResponse(
                        "매매",
                        178000,
                        null,
                        0,
                        LocalDate.of(2026, 5, 10),
                        new BigDecimal("84.93"),
                        12
                )
        );
    }

    private HouseDetailResponse houseDetailResponse() {
        return new HouseDetailResponse(
                1L,
                "역삼래미안",
                "1168010100",
                "757",
                2005,
                "아파트",
                new BigDecimal("37.5006130"),
                new BigDecimal("127.0364310"),
                List.of(new HouseDetailResponse.HouseDealResponse(
                        1L,
                        "매매",
                        178000,
                        null,
                        0,
                        LocalDate.of(2026, 5, 10),
                        new BigDecimal("84.93"),
                        12
                ))
        );
    }
}
