# WBS (작업 분류 체계)

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-14
- 관련 요구사항: 전체
- 관련 문서: [gantt-chart.md](gantt-chart.md)

---

## WBS 목적

프로젝트 전체 작업을 계층적으로 분류하고 각 작업의 산출물, 담당자, 예상 기간, 선행 작업, 상태를 관리한다.

---

## WBS 표

| WBS 코드 | 작업명 | 산출물 | 담당자 | 예상 기간 | 선행 작업 | 상태 |
|---------|--------|--------|--------|-----------|-----------|------|
| **1** | **문서화** | | | | | |
| 1.1 | 프로젝트 개요 작성 | project-overview.md | 미정 | 0.5일 | 없음 | 초안 |
| 1.2 | 기술 스택 확정 | tech-stack.md | 미정 | 0.5일 | 1.1 | 초안 |
| 1.3 | 기능 요구사항 작성 | functional-requirements.md | 미정 | 1일 | 1.1 | 초안 |
| 1.4 | 비기능 요구사항 작성 | non-functional-requirements.md | 미정 | 0.5일 | 1.3 | 초안 |
| 1.5 | 사용자 시나리오 작성 | user-scenarios.md | 미정 | 1일 | 1.3 | 초안 |
| 1.6 | 유스케이스 다이어그램 작성 | usecase-diagram.md + 이미지 | 미정 | 1일 | 1.5 | 작성 예정 |
| 1.7 | API 명세 작성 | api-spec.md | 미정 | 1일 | 1.3 | 초안 |
| 1.8 | 요구사항 추적 매트릭스 작성 | requirement-traceability.md | 미정 | 0.5일 | 1.3, 1.7 | 초안 |
| **2** | **설계** | | | | | |
| 2.1 | 화면 목록 및 흐름 설계 | screen-list.md, screen-flow.md | 미정 | 1일 | 1.3 | 초안 |
| 2.2 | 와이어프레임 제작 | 와이어프레임 이미지 (Figma) | 미정 | 2일 | 2.1 | 작성 예정 |
| 2.3 | ERD 설계 | erd.md + 이미지 | 미정 | 1일 | 1.3 | 초안 |
| 2.4 | 테이블 명세 및 DDL 작성 | table-spec.md, schema.sql | 미정 | 1일 | 2.3 | 초안 |
| 2.5 | 백엔드 아키텍처 설계 | backend-architecture.md | 미정 | 0.5일 | 1.2 | 초안 |
| 2.6 | 패키지 구조 설계 | package-structure.md | 미정 | 0.5일 | 2.5 | 초안 |
| 2.7 | 클래스 다이어그램 작성 | class-diagram.md + 이미지 | 미정 | 1일 | 2.6 | 초안 |
| 2.8 | A* 알고리즘 기획 문서 작성 | astar-route-planning.md | 미정 | 1일 | 2.7 | 초안 |
| **3** | **환경 구성** | | | | | |
| 3.1 | Spring Boot 프로젝트 초기화 | pom.xml / build.gradle | 미정 | 0.5일 | 2.5 | 작성 예정 |
| 3.2 | DB 연결 및 스키마 적용 | schema.sql 실행 결과 | 미정 | 0.5일 | 2.4, 3.1 | 작성 예정 |
| 3.3 | MyBatis 설정 | MyBatisConfig.java | 미정 | 0.5일 | 3.2 | 작성 예정 |
| 3.4 | 공통 에러 처리 구현 | GlobalExceptionHandler.java | 미정 | 0.5일 | 3.1 | 작성 예정 |
| **4** | **백엔드 구현 — 필수 기능** | | | | | |
| 4.1 | 행정구역 코드 데이터 수집 | region_code 테이블 데이터 | 미정 | 1일 | 3.2 | 작성 예정 |
| 4.2 | 주택 거래 데이터 수집 | house, house_deal 테이블 데이터 | 미정 | 2일 | 4.1 | 작성 예정 |
| 4.3 | 주택 검색·상세 조회 API | HouseController, HouseService, HouseMapper | 미정 | 2일 | 4.2 | 작성 예정 |
| 4.4 | 회원 가입·조회·수정·탈퇴 API | MemberController, MemberService, MemberMapper | 미정 | 1.5일 | 3.4 | 작성 예정 |
| 4.5 | 로그인·로그아웃·인증 API | AuthController, AuthService, AuthInterceptor | 미정 | 1일 | 4.4 | 작성 예정 |
| 4.6 | 관심 지역 API | FavoriteController, FavoriteService, FavoriteMapper | 미정 | 1일 | 4.5 | 작성 예정 |
| 4.7 | 공지사항 API | NoticeController, NoticeService, NoticeMapper | 미정 | 1일 | 4.5 | 작성 예정 |
| **5** | **백엔드 구현 — 추가 기능** | | | | | |
| 5.1 | 주변 상권 정보 API | CommercialController, CommercialService | 미정 | 1.5일 | 4.3 | 작성 예정 |
| 5.2 | 주변 환경 정보 API | EnvironmentController, EnvironmentService | 미정 | 1.5일 | 4.3 | 작성 예정 |
| **6** | **백엔드 구현 — 심화 기능** | | | | | |
| 6.1 | 경로 그래프 데이터 구축 | route_node, route_edge 테이블 데이터 | 미정 | 2일 | 3.2 | 작성 예정 |
| 6.2 | A* 알고리즘 구현 | AStarPathFinder.java | 미정 | 2일 | 6.1 | 작성 예정 |
| 6.3 | 경로 탐색 API | RouteController, RouteService, RouteMapper | 미정 | 1일 | 6.2 | 작성 예정 |
| **7** | **프론트엔드 구현** | | | | | |
| 7.1 | 공통 레이아웃 (내비게이션, 푸터) | HTML/CSS/JS | 미정 | 1일 | 2.2 | 작성 예정 |
| 7.2 | 주택 검색·결과 화면 | SCR-SEARCH, SCR-RESULT | 미정 | 2일 | 4.3, 7.1 | 작성 예정 |
| 7.3 | 매물 상세 화면 | SCR-DETAIL | 미정 | 2일 | 4.3, 7.2 | 작성 예정 |
| 7.4 | 로그인·회원 가입 화면 | SCR-LOGIN, SCR-SIGNUP | 미정 | 1일 | 4.4, 7.1 | 작성 예정 |
| 7.5 | 마이페이지·관심 지역 화면 | SCR-MYPAGE, SCR-FAVORITE | 미정 | 1.5일 | 4.6, 7.4 | 작성 예정 |
| 7.6 | 공지사항 화면 | SCR-NOTICE-LIST, SCR-NOTICE-DETAIL, SCR-NOTICE-FORM | 미정 | 1일 | 4.7, 7.1 | 작성 예정 |
| 7.7 | 경로 탐색 패널 | SCR-DETAIL 내 경로 탐색 탭 | 미정 | 1.5일 | 6.3, 7.3 | 작성 예정 |
| 7.8 | 상권·환경 정보 화면 | SCR-COMMERCIAL, SCR-ENVIRONMENT | 미정 | 1일 | 5.1, 5.2, 7.3 | 작성 예정 |
| **8** | **통합 및 정리** | | | | | |
| 8.1 | 프론트-백엔드 연동 확인 | 연동 완료 | 미정 | 1일 | 7.3, 7.5 | 작성 예정 |
| 8.2 | 실행 화면 캡처 및 문서 갱신 | screenshots/, 문서 업데이트 | 미정 | 0.5일 | 8.1 | 작성 예정 |
| 8.3 | 제출 체크리스트 최종 점검 | submission-checklist.md | 미정 | 0.5일 | 8.2 | 작성 예정 |

---

## 상태 정의

| 상태 | 설명 |
|------|------|
| 작성 예정 | 아직 시작하지 않음 |
| 진행 중 | 작업 중 |
| 완료 | 작업 완료 및 검토 통과 |
| 초안 | 문서 초안 작성 완료, 검토 전 |
