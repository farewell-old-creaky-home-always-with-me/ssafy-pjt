package com.ssafy.home.route.service;

import com.ssafy.home.route.algorithm.Haversine;
import com.ssafy.home.route.domain.Edge;
import com.ssafy.home.route.domain.FacilityGraph;
import com.ssafy.home.route.domain.Node;
import com.ssafy.home.route.mapper.dto.FacilityResult;
import com.ssafy.home.route.mapper.FacilityMapper;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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
        List<FacilityResult> facilities = facilityMapper.findAll();
        FacilityGraph graph = new FacilityGraph();

        for (FacilityResult f : facilities) {
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

        connectDisconnectedComponents(graph);

        this.cachedGraph = graph;
    }

    public FacilityGraph getGraph() {
        return cachedGraph;
    }

    private void connectDisconnectedComponents(FacilityGraph graph) {
        List<Node> allNodes = List.copyOf(graph.getNodes().values());
        if (allNodes.size() < 2) return;

        Map<Long, Integer> componentId = new HashMap<>();
        int nextComp = 0;
        for (Node node : allNodes) {
            if (componentId.containsKey(node.getId())) continue;
            int comp = nextComp++;
            Queue<Long> queue = new LinkedList<>();
            queue.add(node.getId());
            componentId.put(node.getId(), comp);
            while (!queue.isEmpty()) {
                long curr = queue.poll();
                for (Edge edge : graph.getAdjacentEdges(curr)) {
                    if (!componentId.containsKey(edge.getToId())) {
                        componentId.put(edge.getToId(), comp);
                        queue.add(edge.getToId());
                    }
                }
            }
        }

        if (nextComp <= 1) return;

        Map<Integer, List<Node>> byComp = new HashMap<>();
        for (Node node : allNodes) {
            byComp.computeIfAbsent(componentId.get(node.getId()), k -> new ArrayList<>()).add(node);
        }

        while (byComp.size() > 1) {
            double bestDist = Double.MAX_VALUE;
            Node bestA = null, bestB = null;
            int bestCompA = -1, bestCompB = -1;

            List<Integer> compIds = List.copyOf(byComp.keySet());
            for (int i = 0; i < compIds.size(); i++) {
                for (int j = i + 1; j < compIds.size(); j++) {
                    for (Node a : byComp.get(compIds.get(i))) {
                        for (Node b : byComp.get(compIds.get(j))) {
                            double dist = Haversine.distance(
                                    a.getLat(), a.getLng(), b.getLat(), b.getLng());
                            if (dist < bestDist) {
                                bestDist = dist;
                                bestA = a;
                                bestB = b;
                                bestCompA = compIds.get(i);
                                bestCompB = compIds.get(j);
                            }
                        }
                    }
                }
            }

            graph.addEdge(new Edge(bestA.getId(), bestB.getId(), bestDist));
            graph.addEdge(new Edge(bestB.getId(), bestA.getId(), bestDist));

            List<Node> merged = byComp.remove(bestCompB);
            byComp.get(bestCompA).addAll(merged);
        }
    }
}
