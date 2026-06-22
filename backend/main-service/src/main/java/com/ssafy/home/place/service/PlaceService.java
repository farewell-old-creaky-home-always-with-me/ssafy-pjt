package com.ssafy.home.place.service;

import static com.ssafy.home.global.exception.ErrorCode.PLACE_DUPLICATE_TYPE;
import static com.ssafy.home.global.exception.ErrorCode.PLACE_FORBIDDEN;
import static com.ssafy.home.global.exception.ErrorCode.PLACE_INVALID_ADDRESS;
import static com.ssafy.home.global.exception.ErrorCode.PLACE_INVALID_COORDINATE;
import static com.ssafy.home.global.exception.ErrorCode.PLACE_INVALID_NAME;
import static com.ssafy.home.global.exception.ErrorCode.PLACE_INVALID_REGION;
import static com.ssafy.home.global.exception.ErrorCode.PLACE_INVALID_TYPE;
import static com.ssafy.home.global.exception.ErrorCode.PLACE_NOT_FOUND;
import static com.ssafy.home.global.exception.ErrorCode.PLACE_OTHER_LIMIT_EXCEEDED;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.place.dto.PlaceCreateRequest;
import com.ssafy.home.place.dto.PlaceResponse;
import com.ssafy.home.place.dto.PlaceType;
import com.ssafy.home.place.dto.PlaceUpdateRequest;
import com.ssafy.home.place.mapper.PlaceMapper;
import com.ssafy.home.place.mapper.dto.PlaceCreateParam;
import com.ssafy.home.place.mapper.dto.PlaceResult;
import com.ssafy.home.place.mapper.dto.PlaceUpdateParam;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private static final int MAX_OTHER_PLACE_COUNT = 5;

    private final PlaceMapper placeMapper;

    @Transactional(readOnly = true)
    public List<PlaceResponse> getPlaces(Long memberId) {
        return placeMapper.findByMemberId(memberId)
                .stream()
                .map(PlaceResponse::from)
                .toList();
    }

    @Transactional
    public PlaceResponse createPlace(Long memberId, PlaceCreateRequest request) {
        PlaceCreateParam place = new PlaceCreateParam();
        place.setMemberId(memberId);
        applyRequest(place, request.placeType(), request.name(), request.address(),
                request.regionCode(), request.latitude(), request.longitude());
        validateCreateLimit(memberId, PlaceType.valueOf(place.getPlaceType()));
        placeMapper.insert(place);
        return PlaceResponse.from(place);
    }

    @Transactional
    public PlaceResponse updatePlace(Long memberId, Long placeId, PlaceUpdateRequest request) {
        PlaceResult existing = requireOwnedPlace(memberId, placeId);
        String previousType = existing.getPlaceType();
        PlaceUpdateParam place = new PlaceUpdateParam();
        place.setId(existing.getId());
        place.setMemberId(existing.getMemberId());
        applyRequest(place, request.placeType(), request.name(), request.address(),
                request.regionCode(), request.latitude(), request.longitude());
        validateUpdateLimit(memberId, previousType, PlaceType.valueOf(place.getPlaceType()));
        placeMapper.updateById(place);
        return PlaceResponse.from(requireOwnedPlace(memberId, placeId));
    }

    @Transactional
    public void deletePlace(Long memberId, Long placeId) {
        requireOwnedPlace(memberId, placeId);
        placeMapper.deleteByIdAndMemberId(placeId, memberId);
    }

    private void applyRequest(
            PlaceCreateParam place,
            String placeType,
            String name,
            String address,
            String regionCode,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        PlaceType normalizedType = normalizePlaceType(placeType);
        place.setPlaceType(normalizedType.name());
        place.setName(normalizeName(name));
        place.setAddress(normalizeAddress(address));
        place.setRegionCode(normalizeRegionCode(regionCode));
        place.setLatitude(validateLatitude(latitude));
        place.setLongitude(validateLongitude(longitude));
    }

    private void applyRequest(
            PlaceUpdateParam place,
            String placeType,
            String name,
            String address,
            String regionCode,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        PlaceType normalizedType = normalizePlaceType(placeType);
        place.setPlaceType(normalizedType.name());
        place.setName(normalizeName(name));
        place.setAddress(normalizeAddress(address));
        place.setRegionCode(normalizeRegionCode(regionCode));
        place.setLatitude(validateLatitude(latitude));
        place.setLongitude(validateLongitude(longitude));
    }

    private PlaceType normalizePlaceType(String placeType) {
        if (placeType == null || placeType.trim().isEmpty()) {
            throw new CustomException(PLACE_INVALID_TYPE);
        }
        try {
            return PlaceType.valueOf(placeType.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new CustomException(PLACE_INVALID_TYPE);
        }
    }

    private String normalizeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new CustomException(PLACE_INVALID_NAME);
        }
        return name.trim();
    }

    private String normalizeAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new CustomException(PLACE_INVALID_ADDRESS);
        }
        return address.trim();
    }

    private String normalizeRegionCode(String regionCode) {
        if (regionCode == null || regionCode.trim().isEmpty()) {
            throw new CustomException(PLACE_INVALID_REGION);
        }
        String normalized = regionCode.trim();
        if (normalized.length() != 10 || !placeMapper.existsByRegionCode(normalized)) {
            throw new CustomException(PLACE_INVALID_REGION);
        }
        return normalized;
    }

    private BigDecimal validateLatitude(BigDecimal latitude) {
        if (latitude == null
                || latitude.compareTo(BigDecimal.valueOf(-90)) < 0
                || latitude.compareTo(BigDecimal.valueOf(90)) > 0) {
            throw new CustomException(PLACE_INVALID_COORDINATE);
        }
        return latitude;
    }

    private BigDecimal validateLongitude(BigDecimal longitude) {
        if (longitude == null
                || longitude.compareTo(BigDecimal.valueOf(-180)) < 0
                || longitude.compareTo(BigDecimal.valueOf(180)) > 0) {
            throw new CustomException(PLACE_INVALID_COORDINATE);
        }
        return longitude;
    }

    private void validateCreateLimit(Long memberId, PlaceType placeType) {
        int count = placeMapper.countByMemberIdAndType(memberId, placeType.name());
        if ((placeType == PlaceType.HOME || placeType == PlaceType.WORK) && count > 0) {
            throw new CustomException(PLACE_DUPLICATE_TYPE);
        }
        if (placeType == PlaceType.OTHER && count >= MAX_OTHER_PLACE_COUNT) {
            throw new CustomException(PLACE_OTHER_LIMIT_EXCEEDED);
        }
    }

    private void validateUpdateLimit(Long memberId, String previousType, PlaceType nextType) {
        if (nextType.name().equals(previousType)) {
            return;
        }
        validateCreateLimit(memberId, nextType);
    }

    private PlaceResult requireOwnedPlace(Long memberId, Long placeId) {
        PlaceResult place = placeMapper.findById(placeId);
        if (place == null) {
            throw new CustomException(PLACE_NOT_FOUND);
        }
        if (!memberId.equals(place.getMemberId())) {
            throw new CustomException(PLACE_FORBIDDEN);
        }
        return place;
    }
}
