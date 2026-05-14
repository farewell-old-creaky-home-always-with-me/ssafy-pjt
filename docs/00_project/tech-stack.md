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
| 언어 | Java | 17 | LTS 버전으로 안정성 확보 |
| 프레임워크 | Spring Boot | 3.x | 설정 자동화로 빠른 개발 시작. Spring MVC 레이어 구조를 그대로 활용 가능 |
| ORM / SQL | MyBatis | 3.x | SQL을 직접 제어하면서 Java 객체 매핑을 처리. 공공 데이터처럼 복잡한 조회 쿼리가 많은 환경에 적합 |
| 빌드 | Maven 또는 Gradle | 미정 | Spring Initializr 기본 설정 따름 |

---

## 데이터베이스

| 항목 | 기술 | 선택 이유 |
|------|------|-----------|
| RDBMS | MySQL 8.x | 공공 데이터 특성상 정형 데이터이므로 관계형 DB가 적합 |
| 스키마 관리 | DDL 수동 관리 (schema.sql) | 프로젝트 규모상 마이그레이션 툴 없이 DDL 직접 관리 |

---

## 프론트엔드

| 항목 | 기술 | 선택 이유 |
|------|------|-----------|
| 마크업 | HTML5 | 웹 표준 |
| 스타일 | CSS3 | 별도 프레임워크 없이 직접 스타일링. 프로젝트 규모에 적합 |
| 스크립트 | JavaScript (Vanilla) | 추가 빌드 도구 없이 바로 개발 가능. Fetch API로 백엔드 통신 |

프론트엔드 프레임워크(React, Vue 등)는 이번 프로젝트 범위에서 사용하지 않는다.

---

## 외부 API

| 서비스 | 목적 | 비고 |
|--------|------|------|
| 국토교통부 아파트·다세대 실거래가 API | 주택 거래 데이터 수집 | 공공 데이터 포털 키 필요 |
| VWorld 행정구역 API | 행정구역 코드 및 경계 데이터 | 공공 데이터 포털 키 필요 |
| 공공 상권 데이터 API | 주변 상권 정보 조회 | 미정 — 데이터 출처 확정 필요 |
| 서울 열린데이터 광장 환경 점검 API | 주변 환경 정보 조회 | 서울시 오픈 API 키 필요 |
| 지도 API | 매물 위치 시각화, 경로 표시 | 미정 — Kakao Map 또는 Naver Map 검토 중 |

---

## 개발 도구

| 도구 | 용도 |
|------|------|
| Git | 버전 관리 |
| GitHub | 원격 저장소, PR 기반 협업 |
| IntelliJ IDEA 또는 Eclipse | 백엔드 개발 IDE |
| VS Code | 프론트엔드 편집 |
| Figma 또는 동등 와이어프레임 도구 | UI 화면 설계 |
| ERD 툴 (미정) | DB 모델링 (ERDCloud, draw.io 등 검토 중) |
| Postman | API 수동 테스트 |
| MySQL Workbench | DB 스키마 확인 및 쿼리 실행 |

---

## 기술 스택 다이어그램 (텍스트)

```
[브라우저 - HTML/CSS/JS]
        │  HTTP
        ▼
[Spring Boot 애플리케이션]
   Controller → Service → Mapper
                              │
                              ▼
                          [MyBatis]
                              │
                              ▼
                         [MySQL 8.x]

[외부 공공 데이터 API] ──► [데이터 수집 모듈] ──► MySQL
[지도 API] ──► 브라우저에서 직접 호출 (미정)
```
