package com.ssafy.home.route.mapper;

import com.ssafy.home.route.mapper.dto.RoutePathParam;
import com.ssafy.home.route.mapper.dto.RouteRequestParam;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RouteMapper {
    void insert(RouteRequestParam entity);
    void insertAll(List<RoutePathParam> paths);
}
