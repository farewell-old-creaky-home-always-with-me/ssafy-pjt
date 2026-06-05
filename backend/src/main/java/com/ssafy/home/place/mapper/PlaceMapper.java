package com.ssafy.home.place.mapper;

import com.ssafy.home.place.dto.PlaceEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PlaceMapper {

    boolean existsRegionCode(@Param("regionCode") String regionCode);

    int countByMemberIdAndType(@Param("memberId") Long memberId, @Param("placeType") String placeType);

    void insertPlace(PlaceEntity place);

    List<PlaceEntity> findByMemberId(@Param("memberId") Long memberId);

    PlaceEntity findById(@Param("placeId") Long placeId);

    void updatePlace(PlaceEntity place);

    int deleteByIdAndMemberId(@Param("placeId") Long placeId, @Param("memberId") Long memberId);
}
