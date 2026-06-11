# A* 경로 탐색 구현 계획

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 매물(house)과 내 장소(place) 사이의 최단 경로를 시설물 기반 A* 알고리즘으로 계산하고 DB에 저장하는 API를 구현한다.

**Architecture:** 서버 시작 시 `facility` 테이블 전체를 불러와 1km 반경 기준 인접 그래프를 메모리에 캐시한다. 각 요청에서 house·place 좌표를 가상 노드(-1, -2)로 추가한 작업 그래프 복사본을 만들고, A* 탐색 결과를 `route_request` + `route_path` 테이블에 저장한다. `GraphCacheService.rebuild()`는 public으로 열어두어 추후 Spring Batch 배치 완료 후 호출할 수 있도록 한다.

**Tech Stack:** Spring Boot 3, MyBatis, MySQL 8, Lombok, JUnit 5, Mockito

---

## 파일 목록

| 작업 | 경로 |
|------|------|
| Modify | `backend/src/main/resources/schema.sql` |
| Modify | `backend/src/main/java/com/ssafy/home/global/exception/ErrorCode.java` |
| Create | `backend/src/main/java/com/ssafy/home/route/domain/Node.java` |
| Create | `backend/src/main/java/com/ssafy/home/route/domain/Edge.java` |
| Create | `backend/src/main/java/com/ssafy/home/route/domain/FacilityGraph.java` |
| Create | `backend/src/main/java/com/ssafy/home/route/algorithm/Haversine.java` |
| Create | `backend/src/main/java/com/ssafy/home/route/algorithm/AStarAlgorithm.java` |
| Create | `backend/src/test/java/com/ssafy/home/route/algorithm/AStarAlgorithmTest.java` |
| Create | `backend/src/main/java/com/ssafy/home/route/dto/FacilityEntity.java` |
| Create | `backend/src/main/java/com/ssafy/home/route/dto/RouteRequest.java` |
| Create | `backend/src/main/java/com/ssafy/home/route/dto/RouteResponse.java` |
| Create | `backend/src/main/java/com/ssafy/home/route/dto/RoutePathPoint.java` |
| Create | `backend/src/main/java/com/ssafy/home/route/dto/RouteRequestEntity.java` |
| Create | `backend/src/main/java/com/ssafy/home/route/dto/RoutePathEntity.java` |
| Create | `backend/src/main/java/com/ssafy/home/route/mapper/FacilityMapper.java` |
| Create | `backend/src/main/java/com/ssafy/home/route/mapper/RouteMapper.java` |
| Create | `backend/src/main/resources/mapper/FacilityMapper.xml` |
| Create | `backend/src/main/resources/mapper/RouteMapper.xml` |
| Create | `backend/src/main/java/com/ssafy/home/route/service/GraphCacheService.java` |
| Create | `backend/src/test/java/com/ssafy/home/route/service/GraphCacheServiceTest.java` |
| Create | `backend/src/main/java/com/ssafy/home/route/service/RouteService.java` |
| Create | `backend/src/main/java/com/ssafy/home/route/controller/RouteController.java` |
| Create | `backend/src/main/java/com/ssafy/home/route/controller/RouteApiDocs.java` |

---

## Task 1: 스키마 수정

schema.sql에서 `route_node`·`route_edge`(정적 그래프 설계)를 제거하고 `facility`·`route_request`·`route_path`(동적 캐시 설계)로 교체한다.

**Files:**
- Modify: `backend/src/main/resources/schema.sql`

- [ ] **Step 1: route_node·route_edge 블록을 새 테이블 3개로 교체**

`schema.sql` 203~230번째 줄(아래 블록)을 찾아 통째로 교체한다.

교체 전:
```sql
-- ============================================================
-- 경로 노드 (A* 알고리즘 그래프 노드)
-- ============================================================
CREATE TABLE IF NOT EXISTS route_node (
    node_id   BIGINT        NOT NULL AUTO_INCREMENT COMMENT '노드 ID',
    latitude  DECIMAL(10,7) NOT NULL COMMENT '위도',
    longitude DECIMAL(10,7) NOT NULL COMMENT '경도',
    node_name VARCHAR(100)           COMMENT '노드 명칭 (교차로명 등, 선택)',

    PRIMARY KEY (node_id),
    INDEX idx_node_location (latitude, longitude)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='경로 탐색 그래프 노드';

-- ============================================================
-- 경로 엣지 (A* 알고리즘 그래프 간선)
-- ============================================================
CREATE TABLE IF NOT EXISTS route_edge (
    edge_id      BIGINT NOT NULL AUTO_INCREMENT COMMENT '엣지 ID',
    from_node_id BIGINT NOT NULL COMMENT '출발 노드 ID',
    to_node_id   BIGINT NOT NULL COMMENT '도착 노드 ID',
    distance     DOUBLE NOT NULL COMMENT '거리 (미터)',

    PRIMARY KEY (edge_id),
    FOREIGN KEY (from_node_id) REFERENCES route_node(node_id),
    FOREIGN KEY (to_node_id) REFERENCES route_node(node_id),
    INDEX idx_edge_from (from_node_id),
    INDEX idx_edge_to (to_node_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='경로 탐색 그래프 엣지';
```

교체 후:
```sql
-- ============================================================
-- 시설물 (A* 그래프 노드 — 지하철역, 학교, 병원, 공원 등)
-- ============================================================
CREATE TABLE IF NOT EXISTS facility (
    facility_id   BIGINT        NOT NULL AUTO_INCREMENT COMMENT '시설물 ID',
    name          VARCHAR(100)  NOT NULL COMMENT '시설물명',
    facility_type VARCHAR(30)   NOT NULL COMMENT '유형: SUBWAY, SCHOOL, HOSPITAL, PARK 등',
    address       VARCHAR(200)           COMMENT '주소',
    latitude      DECIMAL(10,7) NOT NULL COMMENT '위도',
    longitude     DECIMAL(10,7) NOT NULL COMMENT '경도',
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',

    PRIMARY KEY (facility_id),
    INDEX idx_facility_location (latitude, longitude),
    INDEX idx_facility_type (facility_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='시설물 (A* 경로 탐색 노드)';

-- ============================================================
-- A* 경로 탐색 요청 결과
-- ============================================================
CREATE TABLE IF NOT EXISTS route_request (
    route_request_id BIGINT   NOT NULL AUTO_INCREMENT COMMENT '경로 탐색 요청 ID',
    member_id        BIGINT   NOT NULL COMMENT '회원 ID',
    house_id         BIGINT   NOT NULL COMMENT '출발 매물 ID',
    place_id         BIGINT   NOT NULL COMMENT '도착 장소 ID',
    total_dist_m     INT      NOT NULL COMMENT '총 거리 (미터)',
    node_count       INT      NOT NULL COMMENT '경로 좌표 수',
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '요청일시',

    PRIMARY KEY (route_request_id),
    FOREIGN KEY (member_id) REFERENCES member(member_id),
    FOREIGN KEY (house_id)  REFERENCES house(house_id),
    FOREIGN KEY (place_id)  REFERENCES member_place(place_id),
    INDEX idx_route_request_member (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='A* 경로 탐색 요청 결과';

-- ============================================================
-- A* 경로 좌표 목록
-- ============================================================
CREATE TABLE IF NOT EXISTS route_path (
    route_path_id    BIGINT        NOT NULL AUTO_INCREMENT COMMENT '경로 좌표 ID',
    route_request_id BIGINT        NOT NULL COMMENT '경로 탐색 요청 ID',
    seq              INT           NOT NULL COMMENT '순서 (0부터)',
    latitude         DECIMAL(10,7) NOT NULL COMMENT '위도',
    longitude        DECIMAL(10,7) NOT NULL COMMENT '경도',

    PRIMARY KEY (route_path_id),
    FOREIGN KEY (route_request_id) REFERENCES route_request(route_request_id) ON DELETE CASCADE,
    INDEX idx_route_path_request (route_request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='A* 경로 좌표 목록';
```

- [ ] **Step 2: 커밋**

```bash
git add backend/src/main/resources/schema.sql
git commit -m "chore: A* 경로 탐색용 테이블 추가 (facility, route_request, route_path)"
```

---

## Task 2: ErrorCode 추가

**Files:**
- Modify: `backend/src/main/java/com/ssafy/home/global/exception/ErrorCode.java`

- [ ] **Step 1: ROUTE_ 에러 코드 3개 추가**

`ENV_INVALID_RADIUS` 항목 뒤에 아래 3줄을 추가한다.

```java
ROUTE_NO_FACILITIES(HttpStatus.UNPROCESSABLE_ENTITY, "등록된 시설물이 없습니다"),
ROUTE_UNREACHABLE(HttpStatus.UNPROCESSABLE_ENTITY, "출발지 또는 도착지 1km 내에 시설물이 없습니다"),
ROUTE_NOT_FOUND(HttpStatus.UNPROCESSABLE_ENTITY, "경로를 찾을 수 없습니다"),
```

`HttpStatus.UNPROCESSABLE_ENTITY`는 422이다. 기존에 사용된 `HttpStatus`가 이미 import돼 있으므로 추가 import는 불필요하다.

- [ ] **Step 2: 커밋**

```bash
git add backend/src/main/java/com/ssafy/home/global/exception/ErrorCode.java
git commit -m "chore: A* 경로 탐색 에러 코드 추가"
```

---

## Task 3: 도메인 모델 (Node, Edge, FacilityGraph, Haversine)

**Files:**
- Create: `backend/src/main/java/com/ssafy/home/route/domain/Node.java`
- Create: `backend/src/main/java/com/ssafy/home/route/domain/Edge.java`
- Create: `backend/src/main/java/com/ssafy/home/route/domain/FacilityGraph.java`
- Create: `backend/src/main/java/com/ssafy/home/route/algorithm/Haversine.java`

- [ ] **Step 1: Node.java 생성**

```java
package com.ssafy.home.route.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Node {
    private final long id;
    private final double lat;
    private final double lng;
    private final String name;
}
```

- [ ] **Step 2: Edge.java 생성**

```java
package com.ssafy.home.route.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Edge {
    private final long fromId;
    private final long toId;
    private final double distanceM;
}
```

- [ ] **Step 3: FacilityGraph.java 생성**

```java
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
```

- [ ] **Step 4: Haversine.java 생성**

```java
package com.ssafy.home.route.algorithm;

public final class Haversine {

    private static final double EARTH_RADIUS_M = 6_371_000.0;

    private Haversine() {}

    public static double distance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return EARTH_RADIUS_M * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
```

- [ ] **Step 5: 커밋**

```bash
git add backend/src/main/java/com/ssafy/home/route/domain/ \
        backend/src/main/java/com/ssafy/home/route/algorithm/Haversine.java
git commit -m "feat: A* 경로 탐색 도메인 모델 및 Haversine 유틸리티 추가"
```

---

## Task 4: AStarAlgorithm — TDD

**Files:**
- Create: `backend/src/test/java/com/ssafy/home/route/algorithm/AStarAlgorithmTest.java`
- Create: `backend/src/main/java/com/ssafy/home/route/algorithm/AStarAlgorithm.java`

- [ ] **Step 1: AStarAlgorithmTest.java 작성**

```java
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

        assertThat(path).isEmpty();
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
```

- [ ] **Step 2: 테스트 실행 → 컴파일 에러 확인 (AStarAlgorithm 미존재)**

```bash
cd backend && ./gradlew test --tests "com.ssafy.home.route.algorithm.AStarAlgorithmTest" -q 2>&1 | tail -10
```

Expected: `error: cannot find symbol` (AStarAlgorithm 클래스 없음)

- [ ] **Step 3: AStarAlgorithm.java 구현**

```java
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
```

- [ ] **Step 4: 테스트 실행 → PASS 확인**

```bash
cd backend && ./gradlew test --tests "com.ssafy.home.route.algorithm.AStarAlgorithmTest" -q 2>&1 | tail -5
```

Expected:
```
3 tests completed
BUILD SUCCESSFUL
```

- [ ] **Step 5: 커밋**

```bash
git add backend/src/main/java/com/ssafy/home/route/algorithm/AStarAlgorithm.java \
        backend/src/test/java/com/ssafy/home/route/algorithm/AStarAlgorithmTest.java
git commit -m "feat: A* 알고리즘 구현 및 단위 테스트 추가"
```

---

## Task 5: GraphCacheService — TDD

**Files:**
- Create: `backend/src/main/java/com/ssafy/home/route/dto/FacilityEntity.java`
- Create: `backend/src/main/java/com/ssafy/home/route/mapper/FacilityMapper.java`
- Create: `backend/src/test/java/com/ssafy/home/route/service/GraphCacheServiceTest.java`
- Create: `backend/src/main/java/com/ssafy/home/route/service/GraphCacheService.java`

- [ ] **Step 1: FacilityEntity.java 생성**

```java
package com.ssafy.home.route.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityEntity {
    private Long facilityId;
    private String name;
    private String facilityType;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime createdAt;
}
```

- [ ] **Step 2: FacilityMapper.java 인터페이스 생성 (스텁)**

```java
package com.ssafy.home.route.mapper;

import com.ssafy.home.route.dto.FacilityEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FacilityMapper {
    List<FacilityEntity> findAll();
}
```

- [ ] **Step 3: GraphCacheServiceTest.java 작성**

```java
package com.ssafy.home.route.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.ssafy.home.route.domain.FacilityGraph;
import com.ssafy.home.route.dto.FacilityEntity;
import com.ssafy.home.route.mapper.FacilityMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GraphCacheServiceTest {

    @Mock
    private FacilityMapper facilityMapper;

    private GraphCacheService graphCacheService;

    @BeforeEach
    void setUp() {
        graphCacheService = new GraphCacheService(facilityMapper);
    }

    @Test
    void facilitiesWithin1kmAreConnected() {
        // A(37.000, 127.000) — B(37.008, 127.000): Haversine ≈ 890m (1km 이내)
        when(facilityMapper.findAll()).thenReturn(List.of(
                facility(1L, 37.000, 127.000),
                facility(2L, 37.008, 127.000)
        ));

        graphCacheService.rebuild();
        FacilityGraph graph = graphCacheService.getGraph();

        assertThat(graph.getAdjacentEdges(1L)).anyMatch(e -> e.getToId() == 2L);
        assertThat(graph.getAdjacentEdges(2L)).anyMatch(e -> e.getToId() == 1L);
    }

    @Test
    void facilitiesOver1kmAreNotConnected() {
        // A(37.000, 127.000) — B(37.010, 127.000): Haversine ≈ 1112m (1km 초과)
        when(facilityMapper.findAll()).thenReturn(List.of(
                facility(1L, 37.000, 127.000),
                facility(2L, 37.010, 127.000)
        ));

        graphCacheService.rebuild();
        FacilityGraph graph = graphCacheService.getGraph();

        assertThat(graph.getAdjacentEdges(1L)).noneMatch(e -> e.getToId() == 2L);
        assertThat(graph.getAdjacentEdges(2L)).noneMatch(e -> e.getToId() == 1L);
    }

    private FacilityEntity facility(Long id, double lat, double lng) {
        FacilityEntity f = new FacilityEntity();
        f.setFacilityId(id);
        f.setName("시설물-" + id);
        f.setFacilityType("SUBWAY");
        f.setLatitude(BigDecimal.valueOf(lat));
        f.setLongitude(BigDecimal.valueOf(lng));
        return f;
    }
}
```

- [ ] **Step 4: 테스트 실행 → 컴파일 에러 확인 (GraphCacheService 미존재)**

```bash
cd backend && ./gradlew test --tests "com.ssafy.home.route.service.GraphCacheServiceTest" -q 2>&1 | tail -10
```

Expected: `error: cannot find symbol` (GraphCacheService 클래스 없음)

- [ ] **Step 5: GraphCacheService.java 구현**

```java
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
```

- [ ] **Step 6: 테스트 실행 → PASS 확인**

```bash
cd backend && ./gradlew test --tests "com.ssafy.home.route.service.GraphCacheServiceTest" -q 2>&1 | tail -5
```

Expected:
```
2 tests completed
BUILD SUCCESSFUL
```

- [ ] **Step 7: 커밋**

```bash
git add backend/src/main/java/com/ssafy/home/route/dto/FacilityEntity.java \
        backend/src/main/java/com/ssafy/home/route/mapper/FacilityMapper.java \
        backend/src/main/java/com/ssafy/home/route/service/GraphCacheService.java \
        backend/src/test/java/com/ssafy/home/route/service/GraphCacheServiceTest.java
git commit -m "feat: 시설물 그래프 캐시 서비스 구현 및 단위 테스트 추가"
```

---

## Task 6: Mapper 구현 (FacilityMapper XML + RouteMapper)

**Files:**
- Create: `backend/src/main/resources/mapper/FacilityMapper.xml`
- Create: `backend/src/main/java/com/ssafy/home/route/mapper/RouteMapper.java`
- Create: `backend/src/main/resources/mapper/RouteMapper.xml`
- Create: `backend/src/main/java/com/ssafy/home/route/dto/RouteRequestEntity.java`
- Create: `backend/src/main/java/com/ssafy/home/route/dto/RoutePathEntity.java`

- [ ] **Step 1: FacilityMapper.xml 생성**

`mybatis.configuration.map-underscore-to-camel-case: true` 설정으로 `facility_id` → `facilityId`, `facility_type` → `facilityType` 자동 매핑된다.

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ssafy.home.route.mapper.FacilityMapper">

    <select id="findAll" resultType="com.ssafy.home.route.dto.FacilityEntity">
        SELECT
            facility_id,
            name,
            facility_type,
            address,
            latitude,
            longitude,
            created_at
        FROM facility
    </select>
</mapper>
```

- [ ] **Step 2: RouteRequestEntity.java 생성**

```java
package com.ssafy.home.route.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteRequestEntity {
    private Long routeRequestId;
    private Long memberId;
    private Long houseId;
    private Long placeId;
    private int totalDistM;
    private int nodeCount;
    private LocalDateTime createdAt;
}
```

- [ ] **Step 3: RoutePathEntity.java 생성**

```java
package com.ssafy.home.route.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoutePathEntity {
    private Long routePathId;
    private Long routeRequestId;
    private int seq;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
```

- [ ] **Step 4: RouteMapper.java 생성**

```java
package com.ssafy.home.route.mapper;

import com.ssafy.home.route.dto.RoutePathEntity;
import com.ssafy.home.route.dto.RouteRequestEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RouteMapper {
    void insertRouteRequest(RouteRequestEntity entity);
    void insertRoutePaths(List<RoutePathEntity> paths);
}
```

- [ ] **Step 5: RouteMapper.xml 생성**

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ssafy.home.route.mapper.RouteMapper">

    <insert id="insertRouteRequest"
            parameterType="com.ssafy.home.route.dto.RouteRequestEntity"
            useGeneratedKeys="true" keyProperty="routeRequestId">
        INSERT INTO route_request (member_id, house_id, place_id, total_dist_m, node_count)
        VALUES (#{memberId}, #{houseId}, #{placeId}, #{totalDistM}, #{nodeCount})
    </insert>

    <insert id="insertRoutePaths" parameterType="java.util.List">
        INSERT INTO route_path (route_request_id, seq, latitude, longitude)
        VALUES
        <foreach collection="list" item="p" separator=",">
            (#{p.routeRequestId}, #{p.seq}, #{p.latitude}, #{p.longitude})
        </foreach>
    </insert>
</mapper>
```

- [ ] **Step 6: 커밋**

```bash
git add backend/src/main/resources/mapper/FacilityMapper.xml \
        backend/src/main/resources/mapper/RouteMapper.xml \
        backend/src/main/java/com/ssafy/home/route/mapper/RouteMapper.java \
        backend/src/main/java/com/ssafy/home/route/dto/RouteRequestEntity.java \
        backend/src/main/java/com/ssafy/home/route/dto/RoutePathEntity.java
git commit -m "feat: A* 경로 탐색 MyBatis 매퍼 추가"
```

---

## Task 7: RouteService

**Files:**
- Create: `backend/src/main/java/com/ssafy/home/route/dto/RouteRequest.java`
- Create: `backend/src/main/java/com/ssafy/home/route/dto/RoutePathPoint.java`
- Create: `backend/src/main/java/com/ssafy/home/route/dto/RouteResponse.java`
- Create: `backend/src/main/java/com/ssafy/home/route/service/RouteService.java`

- [ ] **Step 1: DTO 3개 생성**

`RouteRequest.java`:
```java
package com.ssafy.home.route.dto;

import jakarta.validation.constraints.NotNull;

public record RouteRequest(
        @NotNull Long houseId,
        @NotNull Long placeId
) {}
```

`RoutePathPoint.java`:
```java
package com.ssafy.home.route.dto;

import java.math.BigDecimal;

public record RoutePathPoint(
        int seq,
        BigDecimal latitude,
        BigDecimal longitude,
        String name
) {}
```

`RouteResponse.java`:
```java
package com.ssafy.home.route.dto;

import java.util.List;

public record RouteResponse(
        Long routeRequestId,
        int totalDistanceM,
        List<RoutePathPoint> path
) {}
```

- [ ] **Step 2: RouteService.java 구현**

`connectToNearby`는 가상 노드(id < 0)를 건너뛰고 실제 시설물 노드에만 연결한다. `placeMapper.findById`는 `PlaceMapper` 인터페이스의 기존 메서드를 그대로 사용한다.

```java
package com.ssafy.home.route.service;

import com.ssafy.home.global.exception.CustomException;
import com.ssafy.home.global.exception.ErrorCode;
import com.ssafy.home.house.dto.HouseDetailRow;
import com.ssafy.home.house.mapper.HouseMapper;
import com.ssafy.home.place.dto.PlaceEntity;
import com.ssafy.home.place.mapper.PlaceMapper;
import com.ssafy.home.route.algorithm.AStarAlgorithm;
import com.ssafy.home.route.algorithm.Haversine;
import com.ssafy.home.route.domain.Edge;
import com.ssafy.home.route.domain.FacilityGraph;
import com.ssafy.home.route.domain.Node;
import com.ssafy.home.route.dto.RoutePathEntity;
import com.ssafy.home.route.dto.RoutePathPoint;
import com.ssafy.home.route.dto.RouteRequest;
import com.ssafy.home.route.dto.RouteRequestEntity;
import com.ssafy.home.route.dto.RouteResponse;
import com.ssafy.home.route.mapper.RouteMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public RouteResponse calculateRoute(Long memberId, RouteRequest request) {
        HouseDetailRow house = houseMapper.findHouseById(request.houseId());
        if (house == null) {
            throw new CustomException(ErrorCode.HOUSE_NOT_FOUND);
        }

        PlaceEntity place = placeMapper.findById(request.placeId());
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

        RouteRequestEntity requestEntity = new RouteRequestEntity();
        requestEntity.setMemberId(memberId);
        requestEntity.setHouseId(request.houseId());
        requestEntity.setPlaceId(request.placeId());
        requestEntity.setTotalDistM(totalDistM);
        requestEntity.setNodeCount(path.size());
        routeMapper.insertRouteRequest(requestEntity);

        List<RoutePathEntity> pathEntities = IntStream.range(0, path.size())
                .mapToObj(i -> {
                    RoutePathEntity pe = new RoutePathEntity();
                    pe.setRouteRequestId(requestEntity.getRouteRequestId());
                    pe.setSeq(i);
                    pe.setLatitude(BigDecimal.valueOf(path.get(i).getLat()));
                    pe.setLongitude(BigDecimal.valueOf(path.get(i).getLng()));
                    return pe;
                })
                .toList();
        routeMapper.insertRoutePaths(pathEntities);

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
        int total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            total += (int) Haversine.distance(
                    path.get(i).getLat(), path.get(i).getLng(),
                    path.get(i + 1).getLat(), path.get(i + 1).getLng());
        }
        return total;
    }
}
```

- [ ] **Step 3: 커밋**

```bash
git add backend/src/main/java/com/ssafy/home/route/dto/ \
        backend/src/main/java/com/ssafy/home/route/service/RouteService.java
git commit -m "feat: A* 경로 탐색 서비스 구현"
```

---

## Task 8: RouteController + RouteApiDocs

**Files:**
- Create: `backend/src/main/java/com/ssafy/home/route/controller/RouteApiDocs.java`
- Create: `backend/src/main/java/com/ssafy/home/route/controller/RouteController.java`

- [ ] **Step 1: RouteApiDocs.java 생성**

```java
package com.ssafy.home.route.controller;

import com.ssafy.home.route.dto.RouteRequest;
import com.ssafy.home.route.dto.RouteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Route", description = "경로 탐색 API")
public interface RouteApiDocs {

    @Operation(
            summary = "A* 최단 경로 탐색",
            description = "매물(houseId)과 내 장소(placeId) 사이의 최단 경로를 시설물 기반 A* 알고리즘으로 계산합니다. 결과는 DB에 저장됩니다."
    )
    RouteResponse calculateRoute(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "경로 탐색 요청", required = true)
            @Valid @RequestBody RouteRequest request,
            @Parameter(hidden = true) HttpSession session
    );
}
```

- [ ] **Step 2: RouteController.java 생성**

```java
package com.ssafy.home.route.controller;

import com.ssafy.home.global.interceptor.LoginRequired;
import com.ssafy.home.route.dto.RouteRequest;
import com.ssafy.home.route.dto.RouteResponse;
import com.ssafy.home.route.service.RouteService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LoginRequired
@RestController
@RequestMapping("/api/route")
@RequiredArgsConstructor
public class RouteController implements RouteApiDocs {

    private final RouteService routeService;

    @PostMapping("/astar")
    @Override
    public RouteResponse calculateRoute(
            @Valid @RequestBody RouteRequest request,
            HttpSession session
    ) {
        return routeService.calculateRoute(getMemberId(session), request);
    }

    private Long getMemberId(HttpSession session) {
        return (Long) session.getAttribute("memberId");
    }
}
```

- [ ] **Step 3: 전체 단위 테스트 실행 확인**

```bash
cd backend && ./gradlew test --tests "com.ssafy.home.route.*" -q 2>&1 | tail -5
```

Expected:
```
5 tests completed
BUILD SUCCESSFUL
```

- [ ] **Step 4: 커밋**

```bash
git add backend/src/main/java/com/ssafy/home/route/controller/
git commit -m "feat: A* 경로 탐색 컨트롤러 및 Swagger 문서 추가"
```

---

## 구현 완료 체크리스트

| 항목 | 확인 |
|------|------|
| `facility`, `route_request`, `route_path` 테이블 DDL | ☐ |
| ROUTE_* ErrorCode 3개 추가 | ☐ |
| Haversine admissible heuristic | ☐ |
| A* closed-set 방식 (중복 방문 방지) | ☐ |
| GraphCacheService @PostConstruct + rebuild() public | ☐ |
| 가상 노드 id < 0 (연결 시 건너뜀) | ☐ |
| route_request useGeneratedKeys (INSERT 후 PK 반환) | ☐ |
| route_path batch INSERT (foreach) | ☐ |
| @LoginRequired + session memberId | ☐ |
| AStarAlgorithmTest 3가지 케이스 | ☐ |
| GraphCacheServiceTest 2가지 케이스 | ☐ |
