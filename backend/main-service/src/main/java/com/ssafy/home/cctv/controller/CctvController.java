package com.ssafy.home.cctv.controller;

import com.ssafy.home.cctv.dto.CctvResponse;
import com.ssafy.home.cctv.service.CctvService;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cctv")
@RequiredArgsConstructor
public class CctvController implements CctvApiDocs {

    private final CctvService cctvService;

    @GetMapping
    @Override
    public ResponseEntity<List<CctvResponse>> getCctvInfos(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(required = false) Integer radius
    ) {
        return ResponseEntity.ok(cctvService.getCctvInfos(lat, lng, radius));
    }
}
