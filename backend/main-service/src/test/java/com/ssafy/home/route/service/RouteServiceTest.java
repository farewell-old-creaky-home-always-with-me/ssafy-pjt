package com.ssafy.home.route.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;

import com.ssafy.home.house.mapper.HouseMapper;
import com.ssafy.home.house.mapper.dto.HouseDetailResult;
import com.ssafy.home.place.mapper.PlaceMapper;
import com.ssafy.home.place.mapper.dto.PlaceResult;
import com.ssafy.home.route.algorithm.AStarPathFinder;
import com.ssafy.home.route.domain.Edge;
import com.ssafy.home.route.domain.FacilityGraph;
import com.ssafy.home.route.domain.Node;
import com.ssafy.home.route.dto.RouteRequest;
import com.ssafy.home.route.dto.RouteResponse;
import com.ssafy.home.route.mapper.RouteMapper;
import com.ssafy.home.route.mapper.dto.RouteRequestParam;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock
    private HouseMapper houseMapper;

    @Mock
    private PlaceMapper placeMapper;

    @Mock
    private GraphCacheService graphCacheService;

    @Mock
    private RouteMapper routeMapper;

    private StubPathFinder pathFinder;
    private RouteService routeService;

    @BeforeEach
    void setUp() {
        pathFinder = new StubPathFinder();
        routeService = new RouteService(houseMapper, placeMapper, graphCacheService, routeMapper, pathFinder);
    }

    @Test
    @DisplayName("경로 탐색 인터페이스로 주입된 구현체를 사용해 경로를 계산한다")
    void calculateRouteUsesInjectedPathFinderInterface() {
        // given
        Node facility = new Node(1L, 37.0005, 127.0000, "시설");
        FacilityGraph graph = new FacilityGraph();
        graph.addNode(facility);
        graph.addEdge(new Edge(1L, 1L, 0.0));

        given(houseMapper.findById(10L)).willReturn(houseDetailResult(10L, "아파트", 37.0000, 127.0000));
        given(placeMapper.findById(20L)).willReturn(placeResult(20L, 1L, "회사", 37.0010, 127.0000));
        given(graphCacheService.getGraph()).willReturn(graph);
        doAnswer(invocation -> {
            RouteRequestParam param = invocation.getArgument(0);
            param.setRouteRequestId(99L);
            return null;
        }).when(routeMapper).insert(any(RouteRequestParam.class));

        // when
        RouteResponse response = routeService.calculateRoute(1L, new RouteRequest(10L, 20L));

        // then
        assertThat(pathFinder.called).isTrue();
        assertThat(response.routeRequestId()).isEqualTo(99L);
        assertThat(response.path()).hasSize(3);
    }

    private HouseDetailResult houseDetailResult(Long houseId, String aptName, double latitude, double longitude) {
        HouseDetailResult result = new HouseDetailResult();
        result.setHouseId(houseId);
        result.setAptName(aptName);
        result.setLatitude(BigDecimal.valueOf(latitude));
        result.setLongitude(BigDecimal.valueOf(longitude));
        return result;
    }

    private PlaceResult placeResult(Long placeId, Long memberId, String name, double latitude, double longitude) {
        PlaceResult result = new PlaceResult();
        result.setId(placeId);
        result.setMemberId(memberId);
        result.setName(name);
        result.setLatitude(BigDecimal.valueOf(latitude));
        result.setLongitude(BigDecimal.valueOf(longitude));
        return result;
    }

    private static class StubPathFinder implements AStarPathFinder {
        private boolean called;

        @Override
        public List<Node> search(FacilityGraph graph, Node start, Node end) {
            called = true;
            List<Node> path = new ArrayList<>();
            path.add(start);
            path.add(graph.getNode(1L));
            path.add(end);
            return path;
        }
    }
}
