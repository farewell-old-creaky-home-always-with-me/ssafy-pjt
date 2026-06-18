# SSAFY HOME Backend

공공 데이터 기반 주택 실거래 정보 REST API 서버

---

## 기술 스택

| 항목 | 내용 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.5 |
| ORM | MyBatis 3.0.5 |
| DB | MySQL 8.x |
| Batch | Spring Batch 5.x |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Security | Spring Security Crypto |
| Build Tool | Gradle |

---

## 사전 요구사항

- JDK 17 이상
- MySQL 8.x 실행 중

---

## 환경 설정

민감 정보(DB, API 키)는 `secret/` **서브모듈**의 `application-secret.yml`에서 관리한다.
템플릿은 `application-secret.example.yml`을 참고한다.

```bash
git submodule update --init backend/src/main/resources/secret
# submodule 내 application-secret.yml에 molit/vworld 키 추가
```

| 항목 | secret yml 키 | 설명 |
|------|---------------|------|
| MySQL | `spring.datasource.*` | 접속 URL, 사용자, 비밀번호 |
| 국토부 API | `molit.service-key`, `molit.apartment-sale-url`, `molit.multi-family-sale-url` | 공공데이터포털 인증키·endpoint |
| VWorld | `vworld.api-key`, `vworld.domain` | Open API 키·등록 도메인 |

Spring Batch 메타 테이블(`BATCH_*`)은 `application.yml`의 `spring.batch.jdbc.initialize-schema: always`로 기동 시 자동 생성된다.

---

## 실행 방법

```bash
# 애플리케이션 실행
./gradlew bootRun

# 테스트 실행
./gradlew test
```

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

---

## 패키지 구조

```
com.ssafy.home
├── member/       # 회원 관리
├── auth/         # 세션 인증
├── house/        # 주택 거래 검색·조회
├── favorite/     # 관심 지역 등록·조회·삭제
├── place/        # 회원 장소 저장·조회
├── commercial/   # 주변 상권 정보
├── environment/  # 주변 환경 정보
├── route/        # A* 경로 탐색
├── notice/       # 공지사항
├── admin/        # 관리자 배치 실행 API
├── batch/        # Spring Batch 공공 데이터 수집
├── external/     # 외부 API 클라이언트 (국토부, VWorld, 서울)
└── global/       # 공통 응답, 예외, 인터셉터
```
