package com.ssafy.home.favorite.mapper;

import com.ssafy.home.favorite.mapper.dto.FavoriteCreateParam;
import com.ssafy.home.favorite.mapper.dto.FavoriteDetailResult;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FavoriteMapper {

    boolean existsByRegionCode(@Param("regionCode") String regionCode);

    boolean existsByMemberIdAndRegionCode(@Param("memberId") Long memberId, @Param("regionCode") String regionCode);

    void insert(FavoriteCreateParam favorite);

    List<FavoriteDetailResult> findByMemberId(@Param("memberId") Long memberId);

    Long findMemberIdById(@Param("favoriteId") Long favoriteId);

    int deleteByIdAndMemberId(@Param("favoriteId") Long favoriteId, @Param("memberId") Long memberId);
}
