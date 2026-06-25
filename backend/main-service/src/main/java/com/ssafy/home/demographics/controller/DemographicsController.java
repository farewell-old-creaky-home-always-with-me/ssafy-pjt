package com.ssafy.home.demographics.controller;

import com.ssafy.home.demographics.dto.DemographicsResponse;
import com.ssafy.home.demographics.service.DemographicsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demographics")
@RequiredArgsConstructor
public class DemographicsController implements DemographicsApiDocs {

    private final DemographicsService demographicsService;

    @GetMapping
    @Override
    public ResponseEntity<DemographicsResponse> getDemographics(
            @RequestParam String sido,
            @RequestParam String sigungu,
            @RequestParam String dong
    ) {
        return ResponseEntity.ok(demographicsService.getDemographics(sido, sigungu, dong));
    }
}
