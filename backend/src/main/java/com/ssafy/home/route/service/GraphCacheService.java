package com.ssafy.home.route.service;

import com.ssafy.home.route.algorithm.Haversine;
import com.ssafy.home.route.domain.Edge;
import com.ssafy.home.route.domain.FacilityGraph;
import com.ssafy.home.route.domain.Node;
import com.ssafy.home.route.dto.FacilityEntity;
import com.ssafy.home.route.mapper.FacilityMapper;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GraphCacheService {

    private static final double CONNECTION_RADIUS_M = 1000.0;

    private final FacilityMapper facilityMapper;
    private volatile FacilityGraph cachedGraph;

    @PostConstruct
    public void rebuild() {
        List<FacilityEntity> facilities = facilityMapper.findAll();
        FacilityGraph graph = new FacilityGraph();

        for (FacilityEntity f : facilities) {
            graph.addNode(new Node(
                    f.getFacilityId(),
                    f.getLatitude().doubleValue(),
                    f.getLongitude().doubleValue(),
                    f.getName()));
        }

        List<Node> nodes = List.copyOf(graph.getNodes().values());
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                Node a = nodes.get(i);
                Node b = nodes.get(j);
                double dist = Haversine.distance(a.getLat(), a.getLng(), b.getLat(), b.getLng());
                if (dist <= CONNECTION_RADIUS_M) {
                    graph.addEdge(new Edge(a.getId(), b.getId(), dist));
                    graph.addEdge(new Edge(b.getId(), a.getId(), dist));
                }
            }
        }

        this.cachedGraph = graph;
    }

    public FacilityGraph getGraph() {
        return cachedGraph;
    }
}
