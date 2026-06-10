package com.ssafy.home.route.algorithm;

import com.ssafy.home.route.domain.Edge;
import com.ssafy.home.route.domain.FacilityGraph;
import com.ssafy.home.route.domain.Node;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class AStarAlgorithm {

    public List<Node> search(FacilityGraph graph, Node start, Node end) {
        if (start.getId() == end.getId()) {
            return List.of();
        }

        record OpenNode(long id, double f) {}

        Map<Long, Double> gScore = new HashMap<>();
        Map<Long, Long> cameFrom = new HashMap<>();
        Set<Long> closed = new HashSet<>();
        PriorityQueue<OpenNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(OpenNode::f));

        gScore.put(start.getId(), 0.0);
        openSet.add(new OpenNode(start.getId(),
                Haversine.distance(start.getLat(), start.getLng(), end.getLat(), end.getLng())));

        while (!openSet.isEmpty()) {
            OpenNode current = openSet.poll();

            if (current.id() == end.getId()) {
                return reconstructPath(graph, cameFrom, end.getId());
            }

            if (closed.contains(current.id())) {
                continue;
            }
            closed.add(current.id());

            double currentG = gScore.getOrDefault(current.id(), Double.MAX_VALUE);

            for (Edge edge : graph.getAdjacentEdges(current.id())) {
                if (closed.contains(edge.getToId())) {
                    continue;
                }
                double tentativeG = currentG + edge.getDistanceM();
                if (tentativeG < gScore.getOrDefault(edge.getToId(), Double.MAX_VALUE)) {
                    gScore.put(edge.getToId(), tentativeG);
                    cameFrom.put(edge.getToId(), current.id());
                    Node neighbor = graph.getNode(edge.getToId());
                    double f = tentativeG + Haversine.distance(
                            neighbor.getLat(), neighbor.getLng(), end.getLat(), end.getLng());
                    openSet.add(new OpenNode(edge.getToId(), f));
                }
            }
        }

        return List.of();
    }

    private List<Node> reconstructPath(FacilityGraph graph, Map<Long, Long> cameFrom, long endId) {
        LinkedList<Node> path = new LinkedList<>();
        long current = endId;
        while (cameFrom.containsKey(current)) {
            path.addFirst(graph.getNode(current));
            current = cameFrom.get(current);
        }
        path.addFirst(graph.getNode(current));
        return new ArrayList<>(path);
    }
}
