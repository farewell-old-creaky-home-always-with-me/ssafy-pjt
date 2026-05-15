package com.ssafy.home.commercial.controller;

import com.ssafy.home.commercial.dto.CommercialResponse;
import com.ssafy.home.commercial.service.CommercialService;
import com.ssafy.home.global.response.ApiResponse;
import com.ssafy.home.global.response.ItemsResponse;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/commercial")
public class CommercialController {

    private final CommercialService commercialService;

    public CommercialController(CommercialService commercialService) {
        this.commercialService = commercialService;
    }

    @GetMapping
    public ApiResponse<ItemsResponse<CommercialResponse>> getCommercials(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(required = false) Integer radius,
            @RequestParam(required = false) String category
    ) {
        return ApiResponse.success(commercialService.getCommercials(lat, lng, radius, category));
    }
}
