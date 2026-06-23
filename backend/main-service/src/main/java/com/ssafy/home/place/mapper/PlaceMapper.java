package com.ssafy.home.place.mapper;

import com.ssafy.home.place.mapper.dto.PlaceCreateParam;
import com.ssafy.home.place.mapper.dto.PlaceResult;
import com.ssafy.home.place.mapper.dto.PlaceUpdateParam;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PlaceMapper {

    boolean existsByRegionCode(@Param("regionCode") String regionCode);

    int countByMemberIdAndType(@Param("memberId") Long memberId, @Param("placeType") String placeType);

    void insert(PlaceCreateParam place);

    List<PlaceResult> findByMemberId(@Param("memberId") Long memberId);

    PlaceResult findById(@Param("placeId") Long placeId);

    void updateById(PlaceUpdateParam place);

    int deleteByIdAndMemberId(@Param("placeId") Long placeId, @Param("memberId") Long memberId);
}
