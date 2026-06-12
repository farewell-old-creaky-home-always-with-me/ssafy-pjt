package com.ssafy.home.route.mapper;

import com.ssafy.home.route.dto.RoutePathEntity;
import com.ssafy.home.route.dto.RouteRequestEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RouteMapper {
    void insertRouteRequest(RouteRequestEntity entity);
    void insertRoutePaths(List<RoutePathEntity> paths);
}
