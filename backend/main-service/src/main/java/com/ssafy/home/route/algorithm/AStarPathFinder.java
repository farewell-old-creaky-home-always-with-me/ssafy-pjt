package com.ssafy.home.route.algorithm;

import com.ssafy.home.route.domain.FacilityGraph;
import com.ssafy.home.route.domain.Node;
import java.util.List;

public interface AStarPathFinder {
    List<Node> search(FacilityGraph graph, Node start, Node end);
}
