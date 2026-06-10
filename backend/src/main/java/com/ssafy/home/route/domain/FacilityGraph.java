package com.ssafy.home.route.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacilityGraph {

    private final Map<Long, Node> nodes = new HashMap<>();
    private final Map<Long, List<Edge>> adjacency = new HashMap<>();

    public void addNode(Node node) {
        nodes.put(node.getId(), node);
    }

    public void addEdge(Edge edge) {
        adjacency.computeIfAbsent(edge.getFromId(), k -> new ArrayList<>()).add(edge);
    }

    public Map<Long, Node> getNodes() {
        return nodes;
    }

    public List<Edge> getAdjacentEdges(long nodeId) {
        return adjacency.getOrDefault(nodeId, List.of());
    }

    public Node getNode(long nodeId) {
        return nodes.get(nodeId);
    }

    public FacilityGraph copy() {
        FacilityGraph copy = new FacilityGraph();
        copy.nodes.putAll(this.nodes);
        for (Map.Entry<Long, List<Edge>> entry : this.adjacency.entrySet()) {
            copy.adjacency.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }
}
