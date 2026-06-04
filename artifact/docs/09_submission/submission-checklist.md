# 제출 체크리스트

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-14
- 관련 요구사항: 전체
- 관련 문서: [final-readme-plan.md](final-readme-plan.md), [../README.md](../README.md)

---

## 필수 제출 항목

### 소스 코드 및 실행 환경

- [ ] 소스 코드가 Git 저장소에 최신 상태로 커밋되어 있다
- [ ] `application.yml` 또는 `application.properties`에 DB 연결 정보가 설정되어 있다 (민감 정보는 주석 처리 또는 `.env.example`로 분리)
- [ ] Spring Boot 애플리케이션이 로컬에서 오류 없이 기동된다
- [ ] 루트 `README.md`에 실행 방법이 명시되어 있다

---

### 데이터베이스

- [ ] ERD 이미지 파일이 `docs/assets/diagrams/` 또는 `docs/04_database/erd.md`에 포함되어 있다
- [ ] DB 스키마 파일 `docs/04_database/schema.sql`이 실행 가능한 상태다
- [ ] Spring Batch 기본 메타데이터 스키마 적용 방법이 문서화되어 있다
- [ ] 최소 테스트 데이터(더미 데이터)가 포함된 SQL 또는 수집 방법이 명시되어 있다 (미정)

---

### 설계 문서

- [ ] 유스케이스 다이어그램 (`docs/02_domain/usecase-diagram.md` + 이미지)
- [ ] 클래스 다이어그램 (`docs/06_backend/class-diagram.md` + 이미지)
  - RouteController, RouteService, RouteMapper, AStarPathFinder, RouteNode, RouteEdge, RouteResult 포함 확인
- [ ] 배치 설계 문서 (`docs/10_batch/batch-overview.md`, `house-deal-collect-job.md`, `batch-operation.md`)
- [ ] Vue 전환 로드맵 문서 (`docs/11_frontend-roadmap/vue-transition-plan.md`, `frontend-api-contract.md`)
- [ ] WBS (`docs/08_schedule/wbs.md`)
- [ ] 간트 차트 (`docs/08_schedule/gantt-chart.md` + 이미지 또는 Mermaid)
- [ ] A* 알고리즘 기획 문서 (`docs/07_algorithm/astar-route-planning.md`)
  - REQ-ROUTE-001 연결 확인
  - g(n), h(n), f(n) 정의 포함 확인
  - route_node, route_edge 연결 명시 확인

---

### 화면 설계 및 실행 캡처

- [ ] 주요 화면 와이어프레임 이미지 (`docs/assets/wireframes/`)
  - SCR-MAIN, SCR-SEARCH, SCR-RESULT, SCR-DETAIL 최소 포함
- [ ] 실행 화면 캡처 (`docs/assets/screenshots/`)
  - 주택 검색 결과 화면
  - 매물 상세 화면
  - 경로 탐색 패널 결과 화면 (REQ-ROUTE-001)
  - 관심 지역 관리 화면
  - 로그인 화면

---

### README

- [ ] 루트 `README.md`가 존재한다
- [ ] 프로젝트 소개 섹션이 있다
- [ ] 기술 스택이 명시되어 있다
- [ ] 주요 기능 목록이 있다
- [ ] 실행 방법이 단계별로 안내되어 있다
- [ ] DB 설정 방법이 포함되어 있다
- [ ] API 요약 또는 링크가 있다
- [ ] 알고리즘 요약(A*)이 포함되어 있다
- [ ] 배치 운영 요약이 포함되어 있다
- [ ] 팀원 역할이 명시되어 있다

---

## 최종 검토 체크리스트

| 항목 | 확인 |
|------|------|
| 모든 API가 Postman 또는 브라우저에서 정상 응답하는가 | [ ] |
| 주택 검색 결과가 정확한 데이터를 반환하는가 | [ ] |
| 로그인·로그아웃이 정상 동작하는가 | [ ] |
| 관심 지역 등록·삭제가 정상 동작하는가 | [ ] |
| 배치 실행 API가 Job 실행 정보를 반환하는가 | [ ] |
| 배치 실행 상태 조회 및 재시작 API가 정상 동작하는가 | [ ] |
| A* 경로 탐색이 결과를 반환하는가 | [ ] |
| 공지사항 CRUD가 정상 동작하는가 | [ ] |
| 비회원이 회원 전용 기능 접근 시 401이 반환되는가 | [ ] |
| 오류 응답이 공통 포맷을 따르는가 | [ ] |
| `schema.sql`을 신규 DB에 실행했을 때 오류가 없는가 | [ ] |
| 민감 정보(비밀번호, API 키)가 소스 코드에 하드코딩되어 있지 않은가 | [ ] |
| REQ-ROUTE-001이 요구사항·UI·API·DB·백엔드·알고리즘 문서에 모두 연결되어 있는가 | [ ] |

---

## 미정 항목 (제출 전 확정 필요)

- [ ] Kakao Maps API 키 발급 완료
- [ ] 공공 상권 데이터 API 출처 확정
- [ ] 서울 환경 데이터 API 키 발급 완료
- [ ] Mermaid 기준 ERD 정제 이미지 생성
- [ ] 클래스 다이어그램 이미지 생성
- [ ] 유스케이스 다이어그램 이미지 생성
- [ ] 와이어프레임 Figma 파일 완성 및 이미지 내보내기
- [ ] 담당자 배정 (WBS 업데이트)
