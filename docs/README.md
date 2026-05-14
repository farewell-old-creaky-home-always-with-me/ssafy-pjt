# SSAFY HOME 문서 허브

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-14
- 관련 요구사항: 전체
- 관련 문서: 모든 하위 문서

---

## 문서 목적

이 디렉토리는 SSAFY HOME 프로젝트의 전체 설계 및 개발 문서를 관리한다. 요구사항 정의부터 DB 설계, API 명세, 배치 설계, 알고리즘 설명, 프론트엔드 전환 준비, 제출 체크리스트까지 하나의 흐름으로 연결되도록 구성했다.

---

## 권장 열람 순서

1. `00_project/project-overview.md` — 프로젝트 전체 개요 파악
2. `00_project/tech-stack.md` — 기술 스택 확인
3. `00_project/document-convention.md` — 문서 작성 규칙 숙지
4. `01_requirements/functional-requirements.md` — 기능 요구사항 확인
5. `01_requirements/non-functional-requirements.md` — 비기능 요구사항 확인
6. `02_domain/domain-overview.md` — 도메인 개념 파악
7. `02_domain/user-scenarios.md` — 주요 사용자 시나리오 확인
8. `03_ui/screen-list.md` → `screen-flow.md` — 화면 목록 및 흐름
9. `04_database/erd.md` → `table-spec.md` → `schema.sql` — DB 설계
10. `05_api/api-overview.md` → `api-spec.md` — API 설계
11. `06_backend/backend-architecture.md` → `package-structure.md` — 백엔드 구조
12. `10_batch/batch-overview.md` → `house-deal-collect-job.md` → `batch-operation.md` — 배치 설계
13. `07_algorithm/astar-route-planning.md` — A* 경로 탐색 알고리즘
14. `11_frontend-roadmap/vue-transition-plan.md` → `frontend-api-contract.md` — 향후 Vue 전환 준비
15. `08_schedule/wbs.md` → `gantt-chart.md` — 일정 관리
16. `01_requirements/requirement-traceability.md` — 요구사항 추적 매트릭스
17. `09_submission/submission-checklist.md` — 최종 제출 점검

---

## 전체 문서 목록 및 상태

| 번호 | 경로 | 제목 | 상태 |
|------|------|------|------|
| 00-1 | [project-overview.md](00_project/project-overview.md) | 프로젝트 개요 | 초안 |
| 00-2 | [tech-stack.md](00_project/tech-stack.md) | 기술 스택 | 초안 |
| 00-3 | [document-convention.md](00_project/document-convention.md) | 문서 작성 규칙 | 초안 |
| 01-1 | [functional-requirements.md](01_requirements/functional-requirements.md) | 기능 요구사항 | 초안 |
| 01-2 | [non-functional-requirements.md](01_requirements/non-functional-requirements.md) | 비기능 요구사항 | 초안 |
| 01-3 | [requirement-traceability.md](01_requirements/requirement-traceability.md) | 요구사항 추적 매트릭스 | 초안 |
| 02-1 | [domain-overview.md](02_domain/domain-overview.md) | 도메인 개요 | 초안 |
| 02-2 | [user-scenarios.md](02_domain/user-scenarios.md) | 사용자 시나리오 | 초안 |
| 02-3 | [usecase-diagram.md](02_domain/usecase-diagram.md) | 유스케이스 다이어그램 | 초안 |
| 03-1 | [screen-list.md](03_ui/screen-list.md) | 화면 목록 | 초안 |
| 03-2 | [screen-flow.md](03_ui/screen-flow.md) | 화면 흐름 | 초안 |
| 03-3 | [wireframe.md](03_ui/wireframe.md) | 와이어프레임 계획 | 작성 예정 |
| 04-1 | [data-source.md](04_database/data-source.md) | 데이터 소스 | 초안 |
| 04-2 | [erd.md](04_database/erd.md) | ERD | 초안 |
| 04-3 | [table-spec.md](04_database/table-spec.md) | 테이블 명세 | 초안 |
| 04-4 | [schema.sql](04_database/schema.sql) | DDL 스크립트 | 초안 |
| 05-1 | [api-overview.md](05_api/api-overview.md) | API 설계 원칙 | 초안 |
| 05-2 | [api-spec.md](05_api/api-spec.md) | API 명세 | 초안 |
| 06-1 | [backend-architecture.md](06_backend/backend-architecture.md) | 백엔드 아키텍처 | 초안 |
| 06-2 | [package-structure.md](06_backend/package-structure.md) | 패키지 구조 | 초안 |
| 06-3 | [class-diagram.md](06_backend/class-diagram.md) | 클래스 다이어그램 | 초안 |
| 06-4 | [error-handling.md](06_backend/error-handling.md) | 에러 처리 | 초안 |
| 07-1 | [astar-route-planning.md](07_algorithm/astar-route-planning.md) | A* 경로 탐색 | 초안 |
| 08-1 | [wbs.md](08_schedule/wbs.md) | WBS | 초안 |
| 08-2 | [gantt-chart.md](08_schedule/gantt-chart.md) | 간트 차트 | 초안 |
| 09-1 | [submission-checklist.md](09_submission/submission-checklist.md) | 제출 체크리스트 | 초안 |
| 09-2 | [final-readme-plan.md](09_submission/final-readme-plan.md) | 최종 README 계획 | 초안 |
| 10-1 | [batch-overview.md](10_batch/batch-overview.md) | 배치 개요 | 초안 |
| 10-2 | [house-deal-collect-job.md](10_batch/house-deal-collect-job.md) | 주택 거래 수집 Job | 초안 |
| 10-3 | [batch-operation.md](10_batch/batch-operation.md) | 배치 운영 | 초안 |
| 11-1 | [vue-transition-plan.md](11_frontend-roadmap/vue-transition-plan.md) | Vue 전환 계획 | 초안 |
| 11-2 | [frontend-api-contract.md](11_frontend-roadmap/frontend-api-contract.md) | 프론트엔드 API 계약 | 초안 |

---

## 문서 의존성 흐름

```text
프로젝트 개요
    │
    ├─► 기술 스택
    │
    ├─► 기능 요구사항 ──────────────────────────────┐
    │       │                                       │
    │       ▼                                       ▼
    │   비기능 요구사항               요구사항 추적 매트릭스
    │       │                           (모든 문서 연결)
    │
    ├─► 도메인 개요 ──► 사용자 시나리오 ──► 유스케이스 다이어그램
    │
    ├─► 화면 목록 ──► 화면 흐름 ──► 와이어프레임
    │                              │
    │                              └─► Vue 전환 계획 ──► 프론트엔드 API 계약
    │
    ├─► 데이터 소스 ──► ERD ──► 테이블 명세 ──► schema.sql
    │
    ├─► API 설계 원칙 ──► API 명세
    │
    ├─► 백엔드 아키텍처 ──► 패키지 구조 ──► 클래스 다이어그램
    │                              │
    │                              └─► 배치 개요 ──► 주택 거래 수집 Job ──► 배치 운영
    │
    ├─► A* 경로 탐색 알고리즘
    │
    ├─► WBS ──► 간트 차트
    │
    └─► 제출 체크리스트 ──► 최종 README 계획
```

---

## 요구사항 추적 설명

모든 기능 요구사항은 `REQ-{도메인}-{번호}` 형식의 ID를 부여한다. 동일 ID가 요구사항 문서, UI 화면 목록, API 명세, DB 테이블, 백엔드 클래스, 알고리즘 문서, 배치 문서에 걸쳐 참조된다.

| ID 접두어 | 도메인 |
|-----------|--------|
| REQ-HOUSE | 주택 거래 정보 |
| REQ-MEMBER | 회원 관리 |
| REQ-AUTH | 인증/인가 |
| REQ-FAVORITE | 관심 지역 |
| REQ-COMMERCIAL | 상권 정보 |
| REQ-ENV | 환경 정보 |
| REQ-ROUTE | 경로 탐색 |
| REQ-NOTICE | 공지사항 |

---

## 최종 제출 문서 체크리스트 요약

> 상세 체크리스트: [09_submission/submission-checklist.md](09_submission/submission-checklist.md)

- [ ] 루트 README.md
- [ ] 소스 코드
- [ ] ERD (`04_database/erd.md`)
- [ ] DB 스키마 (`04_database/schema.sql`)
- [ ] 유스케이스 다이어그램 (`02_domain/usecase-diagram.md`)
- [ ] 클래스 다이어그램 (`06_backend/class-diagram.md`)
- [ ] 배치 설계 문서 (`10_batch/`)
- [ ] Vue 전환 문서 (`11_frontend-roadmap/`)
- [ ] WBS (`08_schedule/wbs.md`)
- [ ] 간트 차트 (`08_schedule/gantt-chart.md`)
- [ ] 화면 설계 캡처 (`assets/wireframes/`, `assets/screenshots/`)
- [ ] 알고리즘 기획 문서 (`07_algorithm/astar-route-planning.md`)
