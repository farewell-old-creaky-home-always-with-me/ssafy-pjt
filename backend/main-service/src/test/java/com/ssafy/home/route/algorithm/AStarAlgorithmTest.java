package com.ssafy.home.route.algorithm;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.route.domain.Edge;
import com.ssafy.home.route.domain.FacilityGraph;
import com.ssafy.home.route.domain.Node;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AStarAlgorithmTest {

    private AStarAlgorithm astar;

    @BeforeEach
    void setUp() {
        astar = new AStarAlgorithm();
    }

    @Test
    @DisplayName("선형 그래프에서 최단 경로를 찾는다")
    void findsShortestPathInLinearGraph() {
        // given — A --890m-- B --890m-- C
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

        // when
        List<Node> path = astar.search(graph, a, c);

        // then
        assertThat(path).extracting(Node::getId).containsExactly(1L, 2L, 3L);
    }

    @Test
    @DisplayName("출발지와 도착지가 같으면 단일 노드 경로를 반환한다")
    void returnsEmptyPathWhenStartEqualsEnd() {
        // given
        Node a = new Node(1L, 37.000, 127.000, "A");
        FacilityGraph graph = new FacilityGraph();
        graph.addNode(a);

        // when
        List<Node> path = astar.search(graph, a, a);

        // then
        assertThat(path).extracting(Node::getId).containsExactly(1L);
    }

    @Test
    @DisplayName("연결되지 않은 노드 사이에는 빈 경로를 반환한다")
    void returnsEmptyPathWhenNoConnectionExists() {
        // given
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

        // when
        List<Node> path = astar.search(graph, a, isolated);

        // then
        assertThat(path).isEmpty();
    }
}
