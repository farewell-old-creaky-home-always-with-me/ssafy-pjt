package com.ssafy.home.place.service;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.exception.ErrorCode;
import com.ssafy.home.place.dto.CreatePlaceRequest;
import com.ssafy.home.place.dto.PlaceEntity;
import com.ssafy.home.place.dto.PlaceResponse;
import com.ssafy.home.place.dto.PlaceType;
import com.ssafy.home.place.dto.UpdatePlaceRequest;
import com.ssafy.home.place.mapper.PlaceMapper;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private static final int MAX_OTHER_PLACE_COUNT = 5;

    private final PlaceMapper placeMapper;

    public List<PlaceResponse> getPlaces(Long memberId) {
        return placeMapper.findByMemberId(memberId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PlaceResponse createPlace(Long memberId, CreatePlaceRequest request) {
        PlaceEntity place = new PlaceEntity();
        place.setMemberId(memberId);
        applyRequest(place, request.placeType(), request.name(), request.address(),
                request.regionCode(), request.latitude(), request.longitude());
        validateCreateLimit(memberId, PlaceType.valueOf(place.getPlaceType()));
        placeMapper.insertPlace(place);
        return toResponse(place);
    }

    public PlaceResponse updatePlace(Long memberId, Long placeId, UpdatePlaceRequest request) {
        PlaceEntity place = requireOwnedPlace(memberId, placeId);
        String previousType = place.getPlaceType();
        applyRequest(place, request.placeType(), request.name(), request.address(),
                request.regionCode(), request.latitude(), request.longitude());
        validateUpdateLimit(memberId, previousType, PlaceType.valueOf(place.getPlaceType()));
        placeMapper.updatePlace(place);
        return toResponse(place);
    }

    public void deletePlace(Long memberId, Long placeId) {
        requireOwnedPlace(memberId, placeId);
        placeMapper.deleteByIdAndMemberId(placeId, memberId);
    }

    private void applyRequest(
            PlaceEntity place,
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
            throw new CustomException(ErrorCode.PLACE_INVALID_TYPE);
        }
        try {
            return PlaceType.valueOf(placeType.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new CustomException(ErrorCode.PLACE_INVALID_TYPE);
        }
    }

    private String normalizeName(String name) {
        if (name == null || name.trim().isEmpty() || name.trim().length() > 50) {
            throw new CustomException(ErrorCode.PLACE_INVALID_NAME);
        }
        return name.trim();
    }

    private String normalizeAddress(String address) {
        if (address == null || address.trim().isEmpty() || address.trim().length() > 150) {
            throw new CustomException(ErrorCode.PLACE_INVALID_ADDRESS);
        }
        return address.trim();
    }

    private String normalizeRegionCode(String regionCode) {
        if (regionCode == null || regionCode.trim().isEmpty()) {
            return null;
        }
        String normalized = regionCode.trim();
        if (normalized.length() != 10 || !placeMapper.existsRegionCode(normalized)) {
            throw new CustomException(ErrorCode.PLACE_INVALID_REGION);
        }
        return normalized;
    }

    private BigDecimal validateLatitude(BigDecimal latitude) {
        if (latitude == null
                || latitude.compareTo(BigDecimal.valueOf(-90)) < 0
                || latitude.compareTo(BigDecimal.valueOf(90)) > 0) {
            throw new CustomException(ErrorCode.PLACE_INVALID_COORDINATE);
        }
        return latitude;
    }

    private BigDecimal validateLongitude(BigDecimal longitude) {
        if (longitude == null
                || longitude.compareTo(BigDecimal.valueOf(-180)) < 0
                || longitude.compareTo(BigDecimal.valueOf(180)) > 0) {
            throw new CustomException(ErrorCode.PLACE_INVALID_COORDINATE);
        }
        return longitude;
    }

    private void validateCreateLimit(Long memberId, PlaceType placeType) {
        int count = placeMapper.countByMemberIdAndType(memberId, placeType.name());
        if ((placeType == PlaceType.HOME || placeType == PlaceType.WORK) && count > 0) {
            throw new CustomException(ErrorCode.PLACE_DUPLICATE_TYPE);
        }
        if (placeType == PlaceType.OTHER && count >= MAX_OTHER_PLACE_COUNT) {
            throw new CustomException(ErrorCode.PLACE_OTHER_LIMIT_EXCEEDED);
        }
    }

    private void validateUpdateLimit(Long memberId, String previousType, PlaceType nextType) {
        if (nextType.name().equals(previousType)) {
            return;
        }
        validateCreateLimit(memberId, nextType);
    }

    private PlaceEntity requireOwnedPlace(Long memberId, Long placeId) {
        PlaceEntity place = placeMapper.findById(placeId);
        if (place == null) {
            throw new CustomException(ErrorCode.PLACE_NOT_FOUND);
        }
        if (!memberId.equals(place.getMemberId())) {
            throw new CustomException(ErrorCode.PLACE_FORBIDDEN);
        }
        return place;
    }

    private PlaceResponse toResponse(PlaceEntity place) {
        return new PlaceResponse(
                place.getId(),
                place.getPlaceType(),
                place.getName(),
                place.getAddress(),
                place.getRegionCode(),
                place.getLatitude(),
                place.getLongitude(),
                place.getCreatedAt(),
                place.getUpdatedAt()
        );
    }
}
