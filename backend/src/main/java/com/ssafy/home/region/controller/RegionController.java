package com.ssafy.home.region.controller;

import com.ssafy.home.region.dto.RegionResponse;
import com.ssafy.home.region.service.RegionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @GetMapping
    public ResponseEntity<List<RegionResponse>> getRegions(
            @RequestParam(required = false) String dong
    ) {
        return ResponseEntity.ok(regionService.getRegions(dong));
    }
}
