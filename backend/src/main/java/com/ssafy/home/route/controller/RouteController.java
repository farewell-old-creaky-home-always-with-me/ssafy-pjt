package com.ssafy.home.route.controller;

import com.ssafy.home.global.interceptor.LoginRequired;
import com.ssafy.home.route.dto.RouteRequest;
import com.ssafy.home.route.dto.RouteResponse;
import com.ssafy.home.route.service.RouteService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LoginRequired
@RestController
@RequestMapping("/api/route")
@RequiredArgsConstructor
public class RouteController implements RouteApiDocs {

    private final RouteService routeService;

    @PostMapping("/astar")
    @Override
    public RouteResponse calculateRoute(
            @Valid @RequestBody RouteRequest request,
            HttpSession session
    ) {
        return routeService.calculateRoute(getMemberId(session), request);
    }

    private Long getMemberId(HttpSession session) {
        return (Long) session.getAttribute("memberId");
    }
}
