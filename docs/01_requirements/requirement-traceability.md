# 요구사항 추적 매트릭스

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-14
- 관련 요구사항: 전체
- 관련 문서: [functional-requirements.md](functional-requirements.md), [../03_ui/screen-list.md](../03_ui/screen-list.md), [../05_api/api-spec.md](../05_api/api-spec.md), [../04_database/table-spec.md](../04_database/table-spec.md), [../06_backend/class-diagram.md](../06_backend/class-diagram.md), [../07_algorithm/astar-route-planning.md](../07_algorithm/astar-route-planning.md)

---

## 매트릭스

| 요구사항 ID | 화면 | API | DB 테이블 | 백엔드 클래스 | 알고리즘 | 문서 링크 |
|------------|------|-----|-----------|--------------|----------|-----------|
| REQ-HOUSE-001 | 없음 (배치) | POST /api/admin/batch/house-deals | house, house_deal, region_code, batch_collection_log | AdminBatchController, BatchJobService, HouseDealCollectJob, BatchCollectionLogListener | 없음 | [기능 요구사항](functional-requirements.md), [배치 개요](../10_batch/batch-overview.md), [주택 거래 수집 Job](../10_batch/house-deal-collect-job.md) |
| REQ-HOUSE-002 | SCR-SEARCH, SCR-RESULT | GET /api/houses | house, house_deal, region_code | HouseController, HouseService, HouseMapper | 없음 | [기능 요구사항](functional-requirements.md) |
| REQ-HOUSE-003 | SCR-DETAIL | GET /api/houses/{houseId} | house, house_deal | HouseController, HouseService, HouseMapper | 없음 | [기능 요구사항](functional-requirements.md) |
| REQ-HOUSE-004 | SCR-RESULT | GET /api/houses | house_deal | HouseMapper | 없음 | [기능 요구사항](functional-requirements.md) |
| REQ-HOUSE-005 | SCR-RESULT | GET /api/houses | house_deal | HouseMapper | 없음 | [기능 요구사항](functional-requirements.md) |
| REQ-MEMBER-001 | SCR-SIGNUP | POST /api/members | member | MemberController, MemberService, MemberMapper | 없음 | [기능 요구사항](functional-requirements.md) |
| REQ-MEMBER-002 | SCR-MYPAGE | GET /api/members/me | member | MemberController, MemberService, MemberMapper | 없음 | [기능 요구사항](functional-requirements.md) |
| REQ-MEMBER-003 | SCR-MYPAGE | PUT /api/members/me | member | MemberController, MemberService, MemberMapper | 없음 | [기능 요구사항](functional-requirements.md) |
| REQ-MEMBER-004 | SCR-MYPAGE | DELETE /api/members/me | member, favorite_area | MemberController, MemberService, MemberMapper | 없음 | [기능 요구사항](functional-requirements.md) |
| REQ-AUTH-001 | SCR-LOGIN | POST /api/auth/login | member | AuthController, AuthService | 없음 | [기능 요구사항](functional-requirements.md) |
| REQ-AUTH-002 | 공통 내비게이션 | POST /api/auth/logout | 없음 | AuthController | 없음 | [기능 요구사항](functional-requirements.md) |
| REQ-AUTH-003 | 공통 내비게이션 | GET /api/auth/me | member | AuthController | 없음 | [기능 요구사항](functional-requirements.md) |
| REQ-FAVORITE-001 | SCR-FAVORITE | POST /api/favorites | favorite_area, region_code | FavoriteController, FavoriteService, FavoriteMapper | 없음 | [기능 요구사항](functional-requirements.md) |
| REQ-FAVORITE-002 | SCR-FAVORITE | GET /api/favorites | favorite_area, region_code | FavoriteController, FavoriteService, FavoriteMapper | 없음 | [기능 요구사항](functional-requirements.md) |
| REQ-FAVORITE-003 | SCR-FAVORITE | DELETE /api/favorites/{favoriteId} | favorite_area | FavoriteController, FavoriteService, FavoriteMapper | 없음 | [기능 요구사항](functional-requirements.md) |
| REQ-COMMERCIAL-001 | SCR-COMMERCIAL, SCR-DETAIL | GET /api/commercial | commercial_area | CommercialController, CommercialService, CommercialMapper | 없음 | [기능 요구사항](functional-requirements.md) |
| REQ-COMMERCIAL-002 | SCR-COMMERCIAL | GET /api/commercial | commercial_area | CommercialMapper | 없음 | [기능 요구사항](functional-requirements.md) |
| REQ-ENV-001 | SCR-ENVIRONMENT, SCR-DETAIL | GET /api/environment | environment_info | EnvironmentController, EnvironmentService, EnvironmentMapper | 없음 | [기능 요구사항](functional-requirements.md) |
| **REQ-ROUTE-001** | **SCR-DETAIL (경로 탐색 패널)** | **GET /api/routes** | **route_node, route_edge** | **RouteController, RouteService, RouteMapper, AStarPathFinder** | **A* 알고리즘** | [기능 요구사항](functional-requirements.md), [A* 알고리즘](../07_algorithm/astar-route-planning.md) |
| REQ-NOTICE-001 | SCR-NOTICE-LIST | GET /api/notices | notice | NoticeController, NoticeService, NoticeMapper | 없음 | [기능 요구사항](functional-requirements.md) |
| REQ-NOTICE-002 | SCR-NOTICE-DETAIL | GET /api/notices/{noticeId} | notice | NoticeController, NoticeService, NoticeMapper | 없음 | [기능 요구사항](functional-requirements.md) |
| REQ-NOTICE-003 | SCR-NOTICE-FORM | POST /api/notices | notice | NoticeController, NoticeService, NoticeMapper | 없음 | [기능 요구사항](functional-requirements.md) |
| REQ-NOTICE-004 | SCR-NOTICE-FORM | PUT /api/notices/{noticeId} | notice | NoticeController, NoticeService, NoticeMapper | 없음 | [기능 요구사항](functional-requirements.md) |
| REQ-NOTICE-005 | SCR-NOTICE-LIST | DELETE /api/notices/{noticeId} | notice | NoticeController, NoticeService, NoticeMapper | 없음 | [기능 요구사항](functional-requirements.md) |

---

## REQ-HOUSE-001 상세 추적

| 추적 항목 | 연결 내용 |
|-----------|-----------|
| 실행 API | POST /api/admin/batch/house-deals |
| 실행 이력 API | GET /api/admin/batch/jobs, GET /api/admin/batch/jobs/{jobExecutionId}, POST /api/admin/batch/jobs/{jobExecutionId}/restart |
| 배치 Job | houseDealCollectJob |
| Step | 공공 데이터 조회 → 검증·정규화 → house/house_deal 저장 → 수집 로그 기록 |
| DB 테이블 | house, house_deal, region_code, batch_collection_log |
| Spring Batch 메타데이터 | Spring Batch 기본 `schema-mysql.sql` 사용 |
| 백엔드 구성 | AdminBatchController, BatchJobService, HouseDealCollectJob, HouseDealCollectStep, MolitHouseDealReader, HouseDealProcessor, HouseDealWriter, BatchCollectionLogListener |
| 배치 문서 | [batch-overview.md](../10_batch/batch-overview.md), [house-deal-collect-job.md](../10_batch/house-deal-collect-job.md), [batch-operation.md](../10_batch/batch-operation.md) |

---

## REQ-ROUTE-001 상세 추적

REQ-ROUTE-001은 A* 경로 탐색 기능의 핵심 요구사항으로, 여러 문서에 걸쳐 추적된다.

| 추적 항목 | 연결 내용 |
|-----------|-----------|
| 화면 | SCR-DETAIL (매물 상세 페이지 내 경로 탐색 패널) |
| API | GET /api/routes?startLat=&startLng=&endLat=&endLng= |
| DB 테이블 | route_node (경로 노드), route_edge (경로 엣지) |
| 백엔드 컨트롤러 | RouteController |
| 백엔드 서비스 | RouteService |
| 백엔드 매퍼 | RouteMapper |
| 알고리즘 클래스 | AStarPathFinder |
| 도메인 클래스 | RouteNode, RouteEdge, RouteResult |
| 알고리즘 문서 | [astar-route-planning.md](../07_algorithm/astar-route-planning.md) |
| 클래스 다이어그램 | [class-diagram.md](../06_backend/class-diagram.md) |
| ERD | [erd.md](../04_database/erd.md) |
| 테이블 명세 | [table-spec.md](../04_database/table-spec.md) |
| API 명세 | [api-spec.md](../05_api/api-spec.md) |

---

## 화면 ID 범례

| 화면 ID | 화면명 |
|---------|--------|
| SCR-MAIN | 메인 페이지 |
| SCR-SEARCH | 주택 검색 페이지 |
| SCR-RESULT | 주택 검색 결과 페이지 |
| SCR-DETAIL | 매물 상세 페이지 |
| SCR-LOGIN | 로그인 페이지 |
| SCR-SIGNUP | 회원 가입 페이지 |
| SCR-MYPAGE | 마이페이지 |
| SCR-FAVORITE | 관심 지역 관리 페이지 |
| SCR-COMMERCIAL | 상권 정보 페이지 |
| SCR-ENVIRONMENT | 환경 정보 페이지 |
| SCR-NOTICE-LIST | 공지사항 목록 페이지 |
| SCR-NOTICE-DETAIL | 공지사항 상세 페이지 |
| SCR-NOTICE-FORM | 공지사항 작성/수정 페이지 |
