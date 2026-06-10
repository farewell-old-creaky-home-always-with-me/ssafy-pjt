# A* 경로 탐색 설계 — 매물 ↔ 내 장소

## 개요

사용자가 관심 매물(house)과 저장된 장소(place) 사이의 최단 경로를 A* 알고리즘으로 계산한다.
시설물(facilities) 좌표를 노드로 삼아 인메모리 그래프를 구성하고, 결과를 DB에 저장한다.

---

## 범위

- **이번 작업**: 그래프 캐시 빌드 + A* 탐색 + DB 저장
- **추후 작업**: Spring Batch 완료 후 `GraphCacheService.rebuild()` 연동

---

## 아키텍처 흐름

```
[RouteController]
    ↓ POST /api/route/astar  { houseId, placeId }
[RouteService]
    ↓ house 좌표, place 좌표 조회
    ↓ GraphCacheService.getGraph() → 캐시된 FacilityGraph
    ↓ 가상 노드(start, end) 추가
    ↓ AStarAlgorithm.search(graph, start, end)
    ↓ 결과 → RouteMapper (route_requests, route_paths INSERT)
    ↓ RouteResponse 반환

[GraphCacheService]
    @PostConstruct → rebuild()
    rebuild() : FacilityMapper로 전체 시설물 조회
               → 1km 이내 쌍 연결 → FacilityGraph 캐시
    (추후 배치 JobExecutionListener가 rebuild() 호출)
```

---

## 패키지 구성

```
route/
  algorithm/   AStarAlgorithm.java
  domain/      FacilityGraph.java, Node.java, Edge.java
  dto/         RouteRequest.java, RouteResponse.java,
               RouteRequestEntity.java, RoutePathEntity.java
  mapper/      FacilityMapper.java, RouteMapper.java
  service/     GraphCacheService.java, RouteService.java
  controller/  RouteController.java, RouteApiDocs.java
resources/mapper/  FacilityMapper.xml, RouteMapper.xml
schema 추가:   facilities, route_requests, route_paths
```

---

## DB 스키마

```sql
-- 시설물 노드
CREATE TABLE facilities (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    type        VARCHAR(30)  NOT NULL,  -- SUBWAY, SCHOOL, HOSPITAL, PARK 등
    address     VARCHAR(200),
    latitude    DECIMAL(10,7) NOT NULL,
    longitude   DECIMAL(10,7) NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- A* 요청 결과 요약
CREATE TABLE route_requests (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id    BIGINT       NOT NULL,
    house_id     BIGINT       NOT NULL,
    place_id     BIGINT       NOT NULL,
    total_dist_m INT          NOT NULL,
    node_count   INT          NOT NULL,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES members(id),
    FOREIGN KEY (house_id)  REFERENCES houses(id),
    FOREIGN KEY (place_id)  REFERENCES places(id)
);

-- 경로 좌표 목록
CREATE TABLE route_paths (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_request_id BIGINT        NOT NULL,
    seq              INT           NOT NULL,
    latitude         DECIMAL(10,7) NOT NULL,
    longitude        DECIMAL(10,7) NOT NULL,
    FOREIGN KEY (route_request_id) REFERENCES route_requests(id)
);
```

---

## 인메모리 도메인 모델

| 클래스 | 역할 |
|---|---|
| `Node` | id, lat, lng, name (DB facility 또는 가상 start/end) |
| `Edge` | from, to, distanceM (Haversine 거리, 미터) |
| `FacilityGraph` | `Map<Long, Node>` + `Map<Long, List<Edge>>` 인접 리스트 |

---

## A* 알고리즘 세부

| 항목 | 결정 |
|---|---|
| heuristic | Haversine(현재 노드 → 목적지) — admissible 보장 |
| 엣지 가중치 | Haversine(두 시설물 간 거리, 미터) |
| 우선순위 큐 | `PriorityQueue<AStarNode>` (f = g + h 기준 min-heap) |
| 가상 노드 | house → start(id=-1), place → end(id=-2), 1km 이내 시설물과 연결 |

---

## API

```
POST /api/route/astar
Body: { "houseId": 123, "placeId": 7 }

Response: {
  "routeRequestId": 42,
  "totalDistanceM": 2340,
  "path": [
    { "seq": 0, "latitude": 37.123, "longitude": 127.456, "name": "출발지" },
    { "seq": 1, "latitude": 37.130, "longitude": 127.460, "name": "강남역" },
    ...
  ]
}
```

---

## 에러 처리

| 상황 | ErrorCode | HTTP |
|---|---|---|
| houseId 존재하지 않음 | `HOUSE_NOT_FOUND` (기존) | 404 |
| placeId 존재하지 않음 | `PLACE_NOT_FOUND` (기존) | 404 |
| place가 요청자 소유 아님 | `PLACE_FORBIDDEN` (기존) | 403 |
| 시설물이 0개 (DB 비어있음) | `ROUTE_NO_FACILITIES` (신규) | 422 |
| start/end 1km 내 시설물 없음 | `ROUTE_UNREACHABLE` (신규) | 422 |
| 경로가 존재하지 않음 (그래프 단절) | `ROUTE_NOT_FOUND` (신규) | 422 |

---

## 테스트 전략

단위 테스트 (JUnit 5):

| 대상 | 검증 내용 |
|---|---|
| `AStarAlgorithm` | 3노드 직선 그래프에서 최단 경로 반환 확인 |
| `AStarAlgorithm` | start = end 인 경우 빈 경로 반환 |
| `AStarAlgorithm` | 고립 노드(연결 없음)에서 `ROUTE_NOT_FOUND` 예외 발생 |
| `GraphCacheService` | `rebuild()` 호출 시 1km 초과 쌍 미연결 확인 |

통합 테스트는 Spring Batch 연동 완료 후 추가한다.
