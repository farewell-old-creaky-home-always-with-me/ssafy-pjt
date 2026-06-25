package com.ssafy.home.housinginfo.controller;

import com.ssafy.home.housinginfo.dto.HousingInfoResponse;
import com.ssafy.home.housinginfo.service.HousingInfoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/housing-info")
@RequiredArgsConstructor
public class HousingInfoController {

    private final HousingInfoService housingInfoService;

    @GetMapping
    public ResponseEntity<List<HousingInfoResponse>> getHousingInfo(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer limit
    ) {
        return ResponseEntity.ok(housingInfoService.getHousingInfo(type, limit));
    }
}
