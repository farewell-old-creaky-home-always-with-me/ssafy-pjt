# 기술 스택

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-14
- 관련 요구사항: 전체
- 관련 문서: [project-overview.md](project-overview.md), [../06_backend/backend-architecture.md](../06_backend/backend-architecture.md)

---

## 백엔드

| 항목 | 기술 | 버전 | 선택 이유 |
|------|------|------|-----------|
| 언어 | Java | 17 | LTS 버전으로 안정성과 생태계 지원이 충분하다 |
| 프레임워크 | Spring Boot | 3.x | REST API 중심 구조, 설정 자동화, 운영 편의성에 적합하다 |
| ORM / SQL | MyBatis | 3.x | SQL 제어가 유연하고 공공 데이터 조회·적재 쿼리 관리에 적합하다 |
| 배치 | Spring Batch | 5.x | 공공 데이터 수집, 변환, 적재를 Job 단위로 관리하고 실행 이력·재시작을 지원한다 |
| 빌드 | Gradle | 8.x | Spring Boot와 궁합이 좋고 의존성 관리, 자동화, 향후 멀티 모듈 확장에 유리하다 |

---

## 데이터베이스

| 항목 | 기술 | 선택 이유 |
|------|------|-----------|
| RDBMS | MySQL 8.x | 정형 데이터 중심 서비스 구조에 적합하다 |
| 서비스 스키마 관리 | `schema.sql` | 서비스 테이블 DDL을 프로젝트 문서와 함께 관리한다 |
| 배치 메타데이터 관리 | Spring Batch 기본 `schema-mysql.sql` | Job/Step 실행 상태를 프레임워크 기본 스키마로 관리한다 |

---

## 프론트엔드

| 항목 | 기술 | 선택 이유 |
|------|------|-----------|
| 마크업 | HTML5 | 웹 표준 기반 구조 |
| 스타일 | CSS3 | 현재 범위에 맞는 직접 스타일링 |
| 스크립트 | JavaScript (Vanilla) | 추가 프레임워크 없이 빠르게 구현 가능하다 |
| 지도 UI | Kakao Maps API | 국내 주소·장소 검색과 매물 위치 시각화, 경로 결과 표시 요구에 적합하다 |

현재 구현 범위는 HTML/CSS/JavaScript다. 다만 백엔드는 API-first로 설계해 향후 Vue.js 전환이 가능하도록 준비한다.

### 향후 프론트엔드 전환 방향

| 항목 | 기술 | 비고 |
|------|------|------|
| 프레임워크 | Vue 3 | 현재 구현 범위에는 포함하지 않는다 |
| 빌드 도구 | Vite | 빠른 개발 서버와 번들링 |
| 라우팅 | Vue Router | 화면 ID 기반 라우팅 전환 준비 |
| 상태 관리 | Pinia | 인증 상태와 공통 UI 상태 관리 |
| HTTP 클라이언트 | Axios | 세션 쿠키 기반 API 호출 처리 |

---

## 외부 API

| 서비스 | 목적 | 비고 |
|--------|------|------|
| 국토교통부 아파트·다세대 실거래가 API | 주택 거래 데이터 수집 | 공공 데이터 포털 키 필요 |
| VWorld 행정구역 API | 행정구역 코드 및 경계 데이터 | 공공 데이터 포털 키 필요 |
| 공공 상권 데이터 API | 주변 상권 정보 조회 | 미정 — 데이터 출처 확정 필요 |
| 서울 열린데이터 광장 환경 점검 API | 주변 환경 정보 조회 | 서울시 오픈 API 키 필요 |
| Kakao Maps API | 매물 위치 시각화, 장소 검색, 경로 결과 표시 | 프론트엔드에서 직접 사용 |

---

## 개발 도구

| 도구 | 용도 |
|------|------|
| Git | 버전 관리 |
| GitHub | 원격 저장소, PR 기반 협업 |
| IntelliJ IDEA 또는 Eclipse | 백엔드 개발 IDE |
| VS Code | 프론트엔드 편집 |
| Figma 또는 동등 와이어프레임 도구 | UI 화면 설계 |
| Mermaid | ERD, 클래스, 유스케이스 다이어그램의 1차 문서화 |
| draw.io 또는 ERDCloud | Mermaid 기반 다이어그램의 후속 시각 정제 |
| Postman | API 수동 테스트 |
| MySQL Workbench | DB 스키마 확인 및 쿼리 실행 |

---

## 기술 스택 다이어그램 (텍스트)

```text
[브라우저 - HTML/CSS/JS]
        │  HTTP / JSON
        ▼
[Spring Boot 3.x 애플리케이션]
   Controller → Service → Mapper
                              │
                              ▼
                        [MyBatis 3.x]
                              │
                              ▼
                         [MySQL 8.x]

[관리자 배치 API] ──► [Spring Batch 5.x Job] ──► [MyBatis] ──► [MySQL]
[국토교통부 / VWorld / 서울 열린데이터] ──► [external client + batch reader]
[Kakao Maps API] ──► 브라우저에서 직접 호출

향후:
[Vue 3 + Vite] ──► 동일한 REST API 재사용
```
