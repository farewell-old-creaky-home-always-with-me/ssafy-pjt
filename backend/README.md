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

아래 환경 변수를 설정하거나 `application.yml`에서 직접 수정한다.

| 환경 변수 | 설명 | 기본값 |
|-----------|------|--------|
| `DB_URL` | MySQL 접속 URL | `jdbc:mysql://localhost:3306/ssafy_home` |
| `DB_USERNAME` | DB 사용자명 | `ssafy` |
| `DB_PASSWORD` | DB 비밀번호 | `ssafy` |
| `SQL_INIT_MODE` | SQL 초기화 모드 | `always` |

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
