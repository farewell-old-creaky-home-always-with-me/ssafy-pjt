package com.ssafy.home.environment.controller;

import com.ssafy.home.environment.dto.EnvironmentResponse;
import com.ssafy.home.environment.service.EnvironmentService;
import com.ssafy.home.global.response.ItemsResponse;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/environment")
@RequiredArgsConstructor
public class EnvironmentController {

    private final EnvironmentService environmentService;

    @GetMapping
    public ItemsResponse<EnvironmentResponse> getEnvironmentInfos(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(required = false) Integer radius
    ) {
        return environmentService.getEnvironmentInfos(lat, lng, radius);
    }
}
