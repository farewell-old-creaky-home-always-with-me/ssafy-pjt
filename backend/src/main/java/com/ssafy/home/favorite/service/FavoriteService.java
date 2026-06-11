package com.ssafy.home.favorite.service;

import com.ssafy.home.favorite.dto.CreateFavoriteRequest;
import com.ssafy.home.favorite.dto.FavoriteCreateResponse;
import com.ssafy.home.favorite.dto.FavoriteEntity;
import com.ssafy.home.favorite.dto.FavoriteResponse;
import com.ssafy.home.favorite.mapper.FavoriteMapper;
import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;

    public List<FavoriteResponse> getFavorites(Long memberId) {
        return favoriteMapper.findByMemberId(memberId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public FavoriteCreateResponse createFavorite(Long memberId, CreateFavoriteRequest request) {
        String regionCode = request.regionCode().trim();
        if (!favoriteMapper.existsRegionCode(regionCode)) {
            throw new CustomException(ErrorCode.HOUSE_INVALID_REGION);
        }
        if (favoriteMapper.existsByMemberIdAndRegionCode(memberId, regionCode)) {
            throw new CustomException(ErrorCode.FAVORITE_DUPLICATE);
        }

        FavoriteEntity favorite = new FavoriteEntity();
        favorite.setMemberId(memberId);
        favorite.setRegionCode(regionCode);

        try {
            favoriteMapper.insertFavorite(favorite);
        } catch (DataIntegrityViolationException ex) {
            throw new CustomException(ErrorCode.FAVORITE_DUPLICATE);
        }

        return new FavoriteCreateResponse(favorite.getId(), regionCode);
    }

    public void deleteFavorite(Long memberId, Long favoriteId) {
        Long ownerMemberId = favoriteMapper.findOwnerMemberIdByFavoriteId(favoriteId);
        if (ownerMemberId == null) {
            throw new CustomException(ErrorCode.FAVORITE_NOT_FOUND);
        }
        if (!ownerMemberId.equals(memberId)) {
            throw new CustomException(ErrorCode.FAVORITE_FORBIDDEN);
        }
        favoriteMapper.deleteByFavoriteIdAndMemberId(favoriteId, memberId);
    }

    private FavoriteResponse toResponse(FavoriteEntity favorite) {
        return new FavoriteResponse(
                favorite.getId(),
                favorite.getRegionCode(),
                favorite.getSidoName(),
                favorite.getSigunguName(),
                favorite.getDongName(),
                favorite.getCreatedAt()
        );
    }
}
