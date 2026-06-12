package com.ssafy.home.route.algorithm;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.route.domain.Edge;
import com.ssafy.home.route.domain.FacilityGraph;
import com.ssafy.home.route.domain.Node;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AStarAlgorithmTest {

    private AStarAlgorithm astar;

    @BeforeEach
    void setUp() {
        astar = new AStarAlgorithm();
    }

    @Test
    void findsShortestPathInLinearGraph() {
        // A(37.000, 127.000) --890m-- B(37.008, 127.000) --890m-- C(37.016, 127.000)
        Node a = new Node(1L, 37.000, 127.000, "A");
        Node b = new Node(2L, 37.008, 127.000, "B");
        Node c = new Node(3L, 37.016, 127.000, "C");

        FacilityGraph graph = new FacilityGraph();
        graph.addNode(a);
        graph.addNode(b);
        graph.addNode(c);

        double abDist = Haversine.distance(a.getLat(), a.getLng(), b.getLat(), b.getLng());
        double bcDist = Haversine.distance(b.getLat(), b.getLng(), c.getLat(), c.getLng());

        graph.addEdge(new Edge(1L, 2L, abDist));
        graph.addEdge(new Edge(2L, 1L, abDist));
        graph.addEdge(new Edge(2L, 3L, bcDist));
        graph.addEdge(new Edge(3L, 2L, bcDist));

        List<Node> path = astar.search(graph, a, c);

        assertThat(path).extracting(Node::getId).containsExactly(1L, 2L, 3L);
    }

    @Test
    void returnsEmptyPathWhenStartEqualsEnd() {
        Node a = new Node(1L, 37.000, 127.000, "A");
        FacilityGraph graph = new FacilityGraph();
        graph.addNode(a);

        List<Node> path = astar.search(graph, a, a);

        assertThat(path).extracting(Node::getId).containsExactly(1L);
    }

    @Test
    void returnsEmptyPathWhenNoConnectionExists() {
        Node a = new Node(1L, 37.000, 127.000, "A");
        Node b = new Node(2L, 37.008, 127.000, "B");
        Node isolated = new Node(3L, 37.500, 128.000, "Isolated");

        FacilityGraph graph = new FacilityGraph();
        graph.addNode(a);
        graph.addNode(b);
        graph.addNode(isolated);

        double abDist = Haversine.distance(a.getLat(), a.getLng(), b.getLat(), b.getLng());
        graph.addEdge(new Edge(1L, 2L, abDist));
        graph.addEdge(new Edge(2L, 1L, abDist));
        // isolated 노드는 엣지 없음

        List<Node> path = astar.search(graph, a, isolated);

        assertThat(path).isEmpty();
    }
}
