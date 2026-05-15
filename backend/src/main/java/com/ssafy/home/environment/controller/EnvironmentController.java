package com.ssafy.home.environment.controller;

import com.ssafy.home.environment.dto.EnvironmentResponse;
import com.ssafy.home.environment.service.EnvironmentService;
import com.ssafy.home.global.response.ApiResponse;
import com.ssafy.home.global.response.ItemsResponse;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/environment")
public class EnvironmentController {

    private final EnvironmentService environmentService;

    public EnvironmentController(EnvironmentService environmentService) {
        this.environmentService = environmentService;
    }

    @GetMapping
    public ApiResponse<ItemsResponse<EnvironmentResponse>> getEnvironmentInfos(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(required = false) Integer radius
    ) {
        return ApiResponse.success(environmentService.getEnvironmentInfos(lat, lng, radius));
    }
}
