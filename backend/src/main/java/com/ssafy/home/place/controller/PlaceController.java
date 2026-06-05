package com.ssafy.home.place.controller;

import com.ssafy.home.global.interceptor.LoginRequired;
import com.ssafy.home.place.dto.CreatePlaceRequest;
import com.ssafy.home.place.dto.PlaceResponse;
import com.ssafy.home.place.dto.UpdatePlaceRequest;
import com.ssafy.home.place.service.PlaceService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LoginRequired
@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController implements PlaceApiDocs {

    private final PlaceService placeService;

    @GetMapping
    @Override
    public List<PlaceResponse> getPlaces(HttpSession session) {
        return placeService.getPlaces(getMemberId(session));
    }

    @PostMapping
    @Override
    public ResponseEntity<PlaceResponse> createPlace(
            @Valid @RequestBody CreatePlaceRequest request,
            HttpSession session
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(placeService.createPlace(getMemberId(session), request));
    }

    @PutMapping("/{placeId}")
    @Override
    public PlaceResponse updatePlace(
            @PathVariable Long placeId,
            @Valid @RequestBody UpdatePlaceRequest request,
            HttpSession session
    ) {
        return placeService.updatePlace(getMemberId(session), placeId, request);
    }

    @DeleteMapping("/{placeId}")
    @Override
    public ResponseEntity<Void> deletePlace(@PathVariable Long placeId, HttpSession session) {
        placeService.deletePlace(getMemberId(session), placeId);
        return ResponseEntity.noContent().build();
    }

    private Long getMemberId(HttpSession session) {
        return (Long) session.getAttribute("memberId");
    }
}
