# A* 경로 탐색 알고리즘

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-14
- 관련 요구사항: REQ-ROUTE-001
- 관련 문서: [../06_backend/class-diagram.md](../06_backend/class-diagram.md), [../05_api/api-spec.md](../05_api/api-spec.md#경로-탐색-api), [../04_database/table-spec.md](../04_database/table-spec.md)

---

## 기능명

A* 기반 최적 경로 탐색 (REQ-ROUTE-001)

---

## 적용 서비스 위치

- **화면**: 매물 상세 페이지 (SCR-DETAIL) 내 경로 탐색 패널
- **API**: `GET /api/routes`
- **클래스**: `RouteController` → `RouteService` → `AStarPathFinder`
- **데이터**: `route_node`, `route_edge` 테이블

---

## 알고리즘 개요

A*(A-star) 알고리즘은 그래프에서 출발 노드에서 목적 노드까지의 최단 경로를 탐색하는 휴리스틱 기반 알고리즘이다. 각 노드의 평가 함수 `f(n) = g(n) + h(n)`을 기준으로 탐색 순서를 결정한다.

---

## A*를 선택한 이유

| 알고리즘 | 특성 | 이 프로젝트 적합성 |
|---------|------|-------------------|
| BFS | 가중치 없는 최단 경로 | 거리 가중치가 있으므로 부적합 |
| Dijkstra | 가중치 있는 최단 경로. 모든 노드 탐색 | 목적지가 명확할 때 불필요한 탐색 발생 |
| A* | 휴리스틱으로 목적지 방향 우선 탐색. 더 빠른 수렴 | 위도·경도 기반 직선거리 휴리스틱 적용 가능. 출발→목적지 단일 쌍 탐색에 최적 |

실제 경로 탐색 서비스의 특성상(출발지·목적지가 명확) A*가 Dijkstra보다 탐색 노드 수를 줄여 응답 속도를 높인다.

---

## 입력 데이터

| 항목 | 타입 | 설명 |
|------|------|------|
| 출발지 위도 (startLat) | Double | 선택된 매물의 위도 |
| 출발지 경도 (startLng) | Double | 선택된 매물의 경도 |
| 목적지 위도 (endLat) | Double | 사용자가 입력한 목적지 위도 |
| 목적지 경도 (endLng) | Double | 사용자가 입력한 목적지 경도 |
| route_node 목록 | List\<RouteNode\> | DB에서 로드한 전체 또는 범위 내 노드 |
| route_edge 목록 | List\<RouteEdge\> | DB에서 로드한 전체 또는 범위 내 엣지 |

---

## 출력 데이터

| 항목 | 타입 | 설명 |
|------|------|------|
| path | List\<RouteNode\> | 출발 → 도착까지의 경로 노드 순서 목록 |
| totalDistance | Double | 총 이동 거리 (미터) |
| estimatedTime | Int | 예상 소요 시간 (분, 보행 속도 기준) |
| searchedNodeCount | Int | A* 탐색 중 방문(closed set 추가)한 노드 수 |

---

## 그래프 모델링 전략

### route_node (노드)

실제 지도 위의 위치 지점을 나타낸다. 교차로, 주요 경유 지점 등을 노드로 정의한다.

- `node_id`: 노드 식별자
- `latitude`, `longitude`: 실세계 좌표
- `node_name`: 노드 명칭 (교차로명 등, 선택)

### route_edge (엣지)

두 노드 간 이동 경로를 나타낸다. 단방향 그래프로 설계한다.

- `from_node_id`: 출발 노드
- `to_node_id`: 도착 노드
- `distance`: 두 노드 간 실제 이동 거리 (미터)

양방향 이동이 가능한 도로는 동일 노드 쌍에 대해 두 방향 엣지를 모두 삽입한다.

---

## 평가 함수 정의

### g(n)

출발 노드에서 현재 노드 `n`까지 실제로 이동한 누적 거리다.

```
g(n) = g(부모 노드) + edge.distance(부모 → n)
```

### h(n) — 휴리스틱 함수

현재 노드 `n`에서 목적 노드까지의 추정 거리다. 위도·경도 기반 **Haversine 직선거리**를 사용한다.

```
h(n) = haversine(n.latitude, n.longitude, goal.latitude, goal.longitude)
```

Haversine 공식은 지구 곡률을 반영한 두 좌표 간 직선거리를 계산한다. 실제 경로 거리 ≥ 직선거리이므로 이 휴리스틱은 **허용 가능(admissible)** 하다. A*의 최적성이 보장된다.

### f(n)

```
f(n) = g(n) + h(n)
```

f(n)이 낮은 노드를 우선 탐색한다.

---

## Open Set과 Closed Set

| 자료구조 | 역할 |
|---------|------|
| Open Set | 탐색 후보 노드 집합. f(n) 기준 최솟값을 빠르게 꺼낼 수 있도록 **우선순위 큐(PriorityQueue)** 사용 |
| Closed Set | 이미 최단 경로가 확정된 노드 집합. **HashSet** 사용. 중복 방문 방지 |

---

## 알고리즘 단계

```
1. 출발지 좌표에서 가장 가까운 route_node를 startNode로 선택
2. 목적지 좌표에서 가장 가까운 route_node를 goalNode로 선택
3. open set에 startNode 추가. g(startNode) = 0
4. open set이 빌 때까지 반복:
   a. f(n) 최솟값 노드 current를 open set에서 꺼냄
   b. current == goalNode이면 경로 재구성 후 반환
   c. current를 closed set에 추가 (방문 확정)
   d. current에서 나가는 엣지(route_edge)를 순회:
      - neighbor가 closed set에 있으면 건너뜀
      - 새 g(neighbor) = g(current) + edge.distance
      - g(neighbor)가 기존값보다 작으면:
        * cameFrom[neighbor] = current
        * g[neighbor] = 새 g 값
        * f[neighbor] = g[neighbor] + h(neighbor)
        * neighbor를 open set에 추가
5. open set이 빌 때까지 goalNode에 도달하지 못하면 ROUTE_NOT_FOUND 반환
```

---

## 경로 재구성

`cameFrom` 맵(HashMap)을 사용해 역추적한다.

```
path = []
current = goalNode
while current != startNode:
    path.prepend(current)
    current = cameFrom[current]
path.prepend(startNode)
return path
```

---

## API 연결

```
GET /api/routes?startLat={}&startLng={}&endLat={}&endLng={}
    └─ RouteController.findRoute()
         └─ RouteService.findRoute()
              ├─ RouteMapper.findNearestNode() → startNode, goalNode
              ├─ RouteMapper.findAllEdges() → 그래프 엣지
              └─ AStarPathFinder.findPath() → RouteResult
                   └─ RouteResponse 변환 후 반환
```

---

## 백엔드 클래스 연결

| 클래스 | 역할 |
|--------|------|
| `RouteController` | API 파라미터 수신 및 응답 반환 |
| `RouteService` | 가장 가까운 노드 찾기, AStarPathFinder 호출, 결과 변환 |
| `RouteMapper` | route_node 및 route_edge 조회 |
| `AStarPathFinder` | A* 알고리즘 구현. RouteNode·RouteEdge 입력, RouteResult 반환 |
| `RouteNode` | 노드 도메인 클래스 |
| `RouteEdge` | 엣지 도메인 클래스 |
| `RouteResult` | 탐색 결과 VO |

---

## 엣지 케이스

| 상황 | 처리 |
|------|------|
| 출발지 주변에 노드 없음 | ROUTE_NODE_NOT_FOUND (404) |
| 목적지 주변에 노드 없음 | ROUTE_NODE_NOT_FOUND (404) |
| 출발지와 목적지가 동일 노드 | 빈 경로 또는 단일 노드 경로 반환 |
| 그래프 내 연결이 끊어진 경우 | ROUTE_NOT_FOUND (422) |
| 탐색 노드 수 한도 초과 | ROUTE_OUT_OF_RANGE (400). 한도값은 미정 |

---

## 한계 및 향후 개선 방향

| 항목 | 현재 한계 | 향후 개선 방향 |
|------|-----------|---------------|
| 그래프 범위 | 사전 구축된 graph 범위로 탐색 제한 | 지도 데이터 연동으로 그래프 자동 생성 |
| 교통 상황 | 거리 기반 가중치만 사용. 실시간 교통 미반영 | 교통 정보 API 연동 시 edge 가중치 동적 조정 |
| 이동 수단 | 보행 기준 단일 속도 가정 | 대중교통, 자전거 등 이동 수단별 가중치 분리 |
| 그래프 크기 | 전체 노드를 메모리에 로드 | 범위 쿼리로 근접 노드만 조회해 메모리 최적화 |
