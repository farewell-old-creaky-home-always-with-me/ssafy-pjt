package com.ssafy.home.route.service;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.exception.ErrorCode;
import com.ssafy.home.house.mapper.dto.HouseDetailResult;
import com.ssafy.home.house.mapper.HouseMapper;
import com.ssafy.home.place.mapper.dto.PlaceResult;
import com.ssafy.home.place.mapper.PlaceMapper;
import com.ssafy.home.route.algorithm.AStarAlgorithm;
import com.ssafy.home.route.algorithm.Haversine;
import com.ssafy.home.route.domain.Edge;
import com.ssafy.home.route.domain.FacilityGraph;
import com.ssafy.home.route.domain.Node;
import com.ssafy.home.route.mapper.dto.RoutePathParam;
import com.ssafy.home.route.dto.RoutePathPoint;
import com.ssafy.home.route.dto.RouteRequest;
import com.ssafy.home.route.mapper.dto.RouteRequestParam;
import com.ssafy.home.route.dto.RouteResponse;
import com.ssafy.home.route.mapper.RouteMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RouteService {

    private static final long START_NODE_ID = -1L;
    private static final long END_NODE_ID = -2L;
    private static final double CONNECTION_RADIUS_M = 1000.0;

    private final HouseMapper houseMapper;
    private final PlaceMapper placeMapper;
    private final GraphCacheService graphCacheService;
    private final RouteMapper routeMapper;
    private final AStarAlgorithm aStarAlgorithm;

    @Transactional
    public RouteResponse calculateRoute(Long memberId, RouteRequest request) {
        HouseDetailResult house = houseMapper.findById(request.houseId());
        if (house == null) {
            throw new CustomException(ErrorCode.HOUSE_NOT_FOUND);
        }

        PlaceResult place = placeMapper.findById(request.placeId());
        if (place == null) {
            throw new CustomException(ErrorCode.PLACE_NOT_FOUND);
        }
        if (!memberId.equals(place.getMemberId())) {
            throw new CustomException(ErrorCode.PLACE_FORBIDDEN);
        }

        FacilityGraph baseGraph = graphCacheService.getGraph();
        if (baseGraph == null || baseGraph.getNodes().isEmpty()) {
            throw new CustomException(ErrorCode.ROUTE_NO_FACILITIES);
        }

        FacilityGraph workGraph = baseGraph.copy();

        Node start = new Node(START_NODE_ID,
                house.getLatitude().doubleValue(), house.getLongitude().doubleValue(), "출발지");
        Node end = new Node(END_NODE_ID,
                place.getLatitude().doubleValue(), place.getLongitude().doubleValue(), place.getName());

        workGraph.addNode(start);
        workGraph.addNode(end);

        if (!connectToNearby(workGraph, start) || !connectToNearby(workGraph, end)) {
            throw new CustomException(ErrorCode.ROUTE_UNREACHABLE);
        }

        List<Node> path = aStarAlgorithm.search(workGraph, start, end);
        if (path.isEmpty()) {
            throw new CustomException(ErrorCode.ROUTE_NOT_FOUND);
        }

        int totalDistM = calculateTotalDistM(path);

        RouteRequestParam requestEntity = new RouteRequestParam();
        requestEntity.setMemberId(memberId);
        requestEntity.setHouseId(request.houseId());
        requestEntity.setPlaceId(request.placeId());
        requestEntity.setTotalDistM(totalDistM);
        requestEntity.setNodeCount(path.size());
        routeMapper.insert(requestEntity);

        List<RoutePathParam> pathEntities = IntStream.range(0, path.size())
                .mapToObj(i -> {
                    RoutePathParam pe = new RoutePathParam();
                    pe.setRouteRequestId(requestEntity.getRouteRequestId());
                    pe.setSeq(i);
                    pe.setLatitude(BigDecimal.valueOf(path.get(i).getLat()));
                    pe.setLongitude(BigDecimal.valueOf(path.get(i).getLng()));
                    return pe;
                })
                .toList();
        routeMapper.insertPaths(pathEntities);

        List<RoutePathPoint> points = IntStream.range(0, path.size())
                .mapToObj(i -> new RoutePathPoint(
                        i,
                        BigDecimal.valueOf(path.get(i).getLat()),
                        BigDecimal.valueOf(path.get(i).getLng()),
                        path.get(i).getName()))
                .toList();

        return new RouteResponse(requestEntity.getRouteRequestId(), totalDistM, points);
    }

    private boolean connectToNearby(FacilityGraph graph, Node node) {
        boolean connected = false;
        for (Node other : graph.getNodes().values()) {
            if (other.getId() < 0) {
                continue; // 가상 노드(start/end) 건너뜀
            }
            double dist = Haversine.distance(
                    node.getLat(), node.getLng(), other.getLat(), other.getLng());
            if (dist <= CONNECTION_RADIUS_M) {
                graph.addEdge(new Edge(node.getId(), other.getId(), dist));
                graph.addEdge(new Edge(other.getId(), node.getId(), dist));
                connected = true;
            }
        }
        return connected;
    }

    private int calculateTotalDistM(List<Node> path) {
        double total = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            total += Haversine.distance(
                    path.get(i).getLat(), path.get(i).getLng(),
                    path.get(i + 1).getLat(), path.get(i + 1).getLng());
        }
        return (int) Math.round(total);
    }
}
