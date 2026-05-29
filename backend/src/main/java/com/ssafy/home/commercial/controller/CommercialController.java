package com.ssafy.home.commercial.controller;

import com.ssafy.home.commercial.dto.CommercialResponse;
import com.ssafy.home.commercial.service.CommercialService;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/commercial")
@RequiredArgsConstructor
public class CommercialController {

    private final CommercialService commercialService;

    @GetMapping
    public List<CommercialResponse> getCommercials(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(required = false) Integer radius,
            @RequestParam(required = false) String category
    ) {
        return commercialService.getCommercials(lat, lng, radius, category);
    }
}
