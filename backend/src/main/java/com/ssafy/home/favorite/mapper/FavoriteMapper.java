package com.ssafy.home.favorite.mapper;

import com.ssafy.home.favorite.dto.FavoriteEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FavoriteMapper {

    boolean existsRegionCode(@Param("regionCode") String regionCode);

    boolean existsByMemberIdAndRegionCode(@Param("memberId") Long memberId, @Param("regionCode") String regionCode);

    void insertFavorite(FavoriteEntity favorite);

    List<FavoriteEntity> findByMemberId(@Param("memberId") Long memberId);

    Long findOwnerMemberIdByFavoriteId(@Param("favoriteId") Long favoriteId);

    int deleteByFavoriteIdAndMemberId(@Param("favoriteId") Long favoriteId, @Param("memberId") Long memberId);
}
