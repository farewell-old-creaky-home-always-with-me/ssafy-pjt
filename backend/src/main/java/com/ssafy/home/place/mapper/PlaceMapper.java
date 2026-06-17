package com.ssafy.home.place.mapper;

import com.ssafy.home.place.mapper.dto.PlaceParam;
import com.ssafy.home.place.mapper.dto.PlaceResult;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PlaceMapper {

    boolean existsRegionCode(@Param("regionCode") String regionCode);

    int countByMemberIdAndType(@Param("memberId") Long memberId, @Param("placeType") String placeType);

    void insert(PlaceParam place);

    List<PlaceResult> findByMemberId(@Param("memberId") Long memberId);

    PlaceResult findById(@Param("placeId") Long placeId);

    void update(PlaceParam place);

    int deleteByIdAndMemberId(@Param("placeId") Long placeId, @Param("memberId") Long memberId);
}
