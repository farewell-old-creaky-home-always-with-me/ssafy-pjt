# 클래스 다이어그램

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-14
- 관련 요구사항: REQ-ROUTE-001, REQ-HOUSE-002, REQ-MEMBER-001, REQ-FAVORITE-001, REQ-NOTICE-001
- 관련 문서: [backend-architecture.md](backend-architecture.md), [package-structure.md](package-structure.md), [../07_algorithm/astar-route-planning.md](../07_algorithm/astar-route-planning.md)

---

## 클래스 목록

### 경로 탐색 도메인 (route)

| 클래스명 | 유형 | 책임 |
|---------|------|------|
| RouteController | Controller | GET /api/routes 엔드포인트 처리. 쿼리 파라미터 수신 후 RouteService 호출 |
| RouteService | Service | 출발·도착 좌표를 받아 가장 가까운 RouteNode를 찾고, 초기 구현에서는 전체 노드·엣지를 메모리에 적재한 뒤 AStarPathFinder를 호출해 경로 계산 |
| RouteMapper | Mapper (MyBatis) | route_node, route_edge 테이블 조회. 초기 구현은 전체 그래프 로드, 인접 엣지 지연 조회는 향후 최적화 대상으로 둔다 |
| AStarPathFinder | Algorithm | RouteNode·RouteEdge 그래프에서 A* 알고리즘으로 최단 경로 탐색 |
| RouteNode | Domain | 그래프 노드. nodeId, latitude, longitude, nodeName 속성 보유 |
| RouteEdge | Domain | 그래프 엣지. edgeId, fromNodeId, toNodeId, distance 속성 보유 |
| RouteResult | Domain | 탐색 결과 VO. path(List\<RouteNode\>), totalDistance, estimatedTime, searchedNodeCount 보유 |

### 주택 도메인 (house)

| 클래스명 | 유형 | 책임 |
|---------|------|------|
| HouseController | Controller | GET /api/houses, GET /api/houses/{id} 처리 |
| HouseService | Service | 주택 검색 조건 처리, 상세 조회, 거래 이력 조합 |
| HouseMapper | Mapper | house, house_deal 조회 |
| HouseCollectService | Service | 공공 API에서 주택 거래 데이터 수집 후 저장 |
| MolitApiClient | Client | 국토교통부 REST API HTTP 호출 |

### 회원/인증 도메인 (member, auth)

| 클래스명 | 유형 | 책임 |
|---------|------|------|
| MemberController | Controller | 회원 CRUD 처리 |
| MemberService | Service | 회원 가입 유효성 검사, 수정, 탈퇴 처리 |
| MemberMapper | Mapper | member 테이블 CRUD |
| AuthController | Controller | 로그인, 로그아웃, 인증 상태 확인 처리 |
| AuthService | Service | 이메일·비밀번호 검증, 세션 관리 |

### 관심 지역 도메인 (favorite)

| 클래스명 | 유형 | 책임 |
|---------|------|------|
| FavoriteController | Controller | 관심 지역 CRUD 처리 |
| FavoriteService | Service | 중복 등록 방지, 소유자 검사 |
| FavoriteMapper | Mapper | favorite_area 테이블 CRUD |

### 공지사항 도메인 (notice)

| 클래스명 | 유형 | 책임 |
|---------|------|------|
| NoticeController | Controller | 공지사항 CRUD 처리 |
| NoticeService | Service | 관리자 권한 확인 후 공지 처리 |
| NoticeMapper | Mapper | notice 테이블 CRUD |

### 공통 (global)

| 클래스명 | 유형 | 책임 |
|---------|------|------|
| GlobalExceptionHandler | @ControllerAdvice | 전체 예외를 공통 오류 응답으로 변환 |
| AuthInterceptor | HandlerInterceptor | 인증 필요 API 세션 유효성 검사 |
| ApiResponse\<T\> | VO | 공통 HTTP 응답 래퍼 |

---

## Mermaid 클래스 다이어그램 — 경로 탐색 도메인

```mermaid
classDiagram
    class RouteController {
        +findRoute(startLat, startLng, endLat, endLng) ApiResponse
    }

    class RouteService {
        -routeMapper RouteMapper
        -pathFinder AStarPathFinder
        +findRoute(startLat, startLng, endLat, endLng) RouteResult
        -findNearestNode(lat, lng) RouteNode
    }

    class RouteMapper {
        +findAllNodes() List~RouteNode~
        +findAllEdges() List~RouteEdge~
        +findNearestNode(lat, lng) RouteNode
        +findEdgesByFromNodeId(fromNodeId) List~RouteEdge~
    }

    class AStarPathFinder {
        +findPath(start, end, edges) RouteResult
        -heuristic(a, b) double
        -reconstructPath(cameFrom, current) List~RouteNode~
    }

    class RouteNode {
        +nodeId long
        +latitude double
        +longitude double
        +nodeName String
    }

    class RouteEdge {
        +edgeId long
        +fromNodeId long
        +toNodeId long
        +distance double
    }

    class RouteResult {
        +path List~RouteNode~
        +totalDistance double
        +estimatedTime int
        +searchedNodeCount int
    }

    RouteController --> RouteService : 호출
    RouteService --> RouteMapper : 조회
    RouteService --> AStarPathFinder : 탐색 위임
    AStarPathFinder --> RouteNode : 사용
    AStarPathFinder --> RouteEdge : 사용
    AStarPathFinder --> RouteResult : 생성
    RouteMapper ..> RouteNode : 반환
    RouteMapper ..> RouteEdge : 반환
```

초기 구현 기준 `RouteMapper`는 `findAllNodes()`, `findAllEdges()`, `findNearestNode(lat, lng)`를 사용한다. `findEdgesByFromNodeId(fromNodeId)`는 그래프 규모가 커질 때 적용할 수 있는 향후 최적화 메서드다.

---

## Mermaid 클래스 다이어그램 — 전체 구조 요약

```mermaid
classDiagram
    class HouseController
    class HouseService
    class HouseMapper

    class MemberController
    class MemberService
    class MemberMapper

    class AuthController
    class AuthService

    class FavoriteController
    class FavoriteService
    class FavoriteMapper

    class NoticeController
    class NoticeService
    class NoticeMapper

    class RouteController
    class RouteService
    class RouteMapper
    class AStarPathFinder

    class GlobalExceptionHandler
    class AuthInterceptor
    class ApiResponse

    HouseController --> HouseService
    HouseService --> HouseMapper
    MemberController --> MemberService
    MemberService --> MemberMapper
    AuthController --> AuthService
    AuthService --> MemberMapper
    FavoriteController --> FavoriteService
    FavoriteService --> FavoriteMapper
    NoticeController --> NoticeService
    NoticeService --> NoticeMapper
    RouteController --> RouteService
    RouteService --> RouteMapper
    RouteService --> AStarPathFinder
```

---

## 클래스 다이어그램 작성 가이드

1. **도구**: PlantUML, draw.io, IntelliJ UML 플러그인 중 하나를 선택한다 (미정).
2. **저장 위치**: `assets/diagrams/class-{도메인}-YYYYMMDD.png`
3. **표기**: UML 표준 클래스 다이어그램 표기법을 사용한다.
4. **포함 범위**: 레이어 간 의존 관계, 주요 메서드 시그니처, 도메인 클래스 속성을 포함한다.
5. **REQ-ROUTE-001 관련 클래스는 반드시 포함**한다: RouteController, RouteService, RouteMapper, AStarPathFinder, RouteNode, RouteEdge, RouteResult.
