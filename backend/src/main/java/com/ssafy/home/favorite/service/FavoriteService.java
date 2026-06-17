package com.ssafy.home.favorite.service;

import com.ssafy.home.favorite.dto.FavoriteCreateRequest;
import com.ssafy.home.favorite.dto.FavoriteCreateResponse;
import com.ssafy.home.favorite.dto.FavoriteResponse;
import com.ssafy.home.favorite.mapper.FavoriteMapper;
import com.ssafy.home.favorite.mapper.dto.FavoriteCreateParam;
import com.ssafy.home.favorite.mapper.dto.FavoriteDetailResult;
import com.ssafy.home.global.exception.CustomException;
import static com.ssafy.home.global.exception.ErrorCode.FAVORITE_DUPLICATE;
import static com.ssafy.home.global.exception.ErrorCode.FAVORITE_FORBIDDEN;
import static com.ssafy.home.global.exception.ErrorCode.FAVORITE_NOT_FOUND;
import static com.ssafy.home.global.exception.ErrorCode.HOUSE_INVALID_REGION;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;

    @Transactional(readOnly = true)
    public List<FavoriteResponse> getFavorites(Long memberId) {
        return favoriteMapper.findByMemberId(memberId)
                .stream()
                .map(FavoriteResponse::from)
                .toList();
    }

    @Transactional
    public FavoriteCreateResponse createFavorite(Long memberId, FavoriteCreateRequest request) {
        String regionCode = request.regionCode().trim();
        if (!favoriteMapper.existsByRegionCode(regionCode)) {
            throw new CustomException(HOUSE_INVALID_REGION);
        }
        if (favoriteMapper.existsByMemberIdAndRegionCode(memberId, regionCode)) {
            throw new CustomException(FAVORITE_DUPLICATE);
        }

        FavoriteCreateParam param = new FavoriteCreateParam();
        param.setMemberId(memberId);
        param.setRegionCode(regionCode);

        try {
            favoriteMapper.insert(param);
        } catch (DataIntegrityViolationException ex) {
            throw new CustomException(FAVORITE_DUPLICATE);
        }

        return FavoriteCreateResponse.of(param.getId(), regionCode);
    }

    @Transactional
    public void deleteFavorite(Long memberId, Long favoriteId) {
        Long ownerMemberId = favoriteMapper.findMemberIdById(favoriteId);
        if (ownerMemberId == null) {
            throw new CustomException(FAVORITE_NOT_FOUND);
        }
        if (!ownerMemberId.equals(memberId)) {
            throw new CustomException(FAVORITE_FORBIDDEN);
        }
        favoriteMapper.deleteByIdAndMemberId(favoriteId, memberId);
    }

}
