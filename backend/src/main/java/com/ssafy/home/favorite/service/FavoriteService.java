package com.ssafy.home.favorite.service;

import com.ssafy.home.favorite.dto.CreateFavoriteRequest;
import com.ssafy.home.favorite.dto.FavoriteCreateResponse;
import com.ssafy.home.favorite.dto.FavoriteEntity;
import com.ssafy.home.favorite.dto.FavoriteResponse;
import com.ssafy.home.favorite.mapper.FavoriteMapper;
import com.ssafy.home.global.exception.DuplicateResourceException;
import com.ssafy.home.global.exception.ForbiddenException;
import com.ssafy.home.global.exception.ResourceNotFoundException;
import com.ssafy.home.global.exception.ValidationException;
import com.ssafy.home.global.response.ItemsResponse;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;

    public FavoriteService(FavoriteMapper favoriteMapper) {
        this.favoriteMapper = favoriteMapper;
    }

    public ItemsResponse<FavoriteResponse> getFavorites(Long memberId) {
        List<FavoriteResponse> items = favoriteMapper.findByMemberId(memberId)
                .stream()
                .map(this::toResponse)
                .toList();
        return new ItemsResponse<>(items);
    }

    public FavoriteCreateResponse createFavorite(Long memberId, CreateFavoriteRequest request) {
        String regionCode = request.regionCode().trim();
        if (!favoriteMapper.existsRegionCode(regionCode)) {
            throw new ValidationException("HOUSE_INVALID_REGION", "유효하지 않은 행정구역 코드입니다");
        }
        if (favoriteMapper.existsByMemberIdAndRegionCode(memberId, regionCode)) {
            throw new DuplicateResourceException("FAVORITE_DUPLICATE", "이미 등록된 관심 지역입니다");
        }

        FavoriteEntity favorite = new FavoriteEntity();
        favorite.setMemberId(memberId);
        favorite.setRegionCode(regionCode);

        try {
            favoriteMapper.insertFavorite(favorite);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateResourceException("FAVORITE_DUPLICATE", "이미 등록된 관심 지역입니다");
        }

        return new FavoriteCreateResponse(favorite.getFavoriteId(), regionCode);
    }

    public void deleteFavorite(Long memberId, Long favoriteId) {
        Long ownerMemberId = favoriteMapper.findOwnerMemberIdByFavoriteId(favoriteId);
        if (ownerMemberId == null) {
            throw new ResourceNotFoundException("FAVORITE_NOT_FOUND", "해당 관심 지역을 찾을 수 없습니다");
        }
        if (!ownerMemberId.equals(memberId)) {
            throw new ForbiddenException("FAVORITE_FORBIDDEN", "본인 관심 지역만 삭제할 수 있습니다");
        }
        favoriteMapper.deleteByFavoriteIdAndMemberId(favoriteId, memberId);
    }

    private FavoriteResponse toResponse(FavoriteEntity favorite) {
        return new FavoriteResponse(
                favorite.getFavoriteId(),
                favorite.getRegionCode(),
                favorite.getSidoName(),
                favorite.getSigunguName(),
                favorite.getDongName(),
                favorite.getCreatedAt()
        );
    }
}
