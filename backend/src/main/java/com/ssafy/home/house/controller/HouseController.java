package com.ssafy.home.house.controller;

import com.ssafy.home.global.response.PageResponse;
import com.ssafy.home.house.dto.HouseDetailResponse;
import com.ssafy.home.house.dto.HouseSummaryResponse;
import com.ssafy.home.house.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/houses")
@RequiredArgsConstructor
public class HouseController {

    private final HouseService houseService;

    @GetMapping
    public PageResponse<HouseSummaryResponse> searchHouses(
            @RequestParam String regionCode,
            @RequestParam(required = false) String houseType,
            @RequestParam(required = false) String dealType,
            @RequestParam(required = false) Integer minAmount,
            @RequestParam(required = false) Integer maxAmount,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return houseService.searchHouses(regionCode, houseType, dealType, minAmount, maxAmount, page, size);
    }

    @GetMapping("/{houseId}")
    public HouseDetailResponse getHouseDetail(@PathVariable Long houseId) {
        return houseService.getHouseDetail(houseId);
    }
}
