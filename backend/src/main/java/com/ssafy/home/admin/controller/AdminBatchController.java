package com.ssafy.home.admin.controller;

import com.ssafy.home.admin.dto.HouseDealCollectRequest;
import com.ssafy.home.admin.dto.HouseDealCollectResponse;
import com.ssafy.home.admin.service.BatchJobService;
import com.ssafy.home.global.auth.LoginMemberId;
import com.ssafy.home.global.interceptor.AdminOnly;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AdminOnly
@RestController
@RequestMapping("/api/admin/batch")
@RequiredArgsConstructor
public class AdminBatchController {

    private final BatchJobService batchJobService;

    @PostMapping("/house-deals")
    public HouseDealCollectResponse collectHouseDeals(
            @Valid @RequestBody HouseDealCollectRequest request,
            @LoginMemberId Long memberId
    ) {
        return batchJobService.collectHouseDeals(memberId, request);
    }
}
