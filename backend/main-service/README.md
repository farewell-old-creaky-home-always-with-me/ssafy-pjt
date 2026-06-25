# Main Service

사용자 웹에서 사용하는 핵심 API 서비스입니다. 회원/인증, 주택 실거래가 검색, 관심 지역/장소, 생활 정보, 게시판, 공지 조회, 배치 리포트 조회 등을 담당합니다.

## 기술 스택

- Java 17
- Spring Boot 3.5
- Spring MVC
- Spring Security
- MyBatis
- Flyway
- MySQL 8
- SpringDoc OpenAPI
- Gradle

## 주요 기능

- 회원 가입, 로그인, 로그아웃, 내 정보 관리
- 주택 실거래가, 지역, 주택 상세 정보 조회
- 관심 지역/매물/장소 관리
- 상권, 환경, 인구, CCTV 정보 조회
- 경로 탐색 API
- 공지사항 조회
- 게시판/QnA CRUD
- 배치 리포트 조회

## 실행

백엔드 루트에서 실행합니다.

```bash
cd backend
./gradlew :main-service:bootRun
```

기본 포트: `8082`

Gateway를 함께 실행하면 외부에서는 `http://localhost:8080/api/**` 경로로 접근합니다.

## 테스트

```bash
cd backend
./gradlew :main-service:test
```

## 주요 구조

```text
src/main/java/com/ssafy/home/
├── auth/          # 인증 API
├── member/        # 회원 API
├── house/         # 주택 실거래가 API
├── region/        # 지역 API
├── favorite/      # 관심 지역/매물 API
├── place/         # 저장 장소 API
├── commercial/    # 상권 API
├── environment/   # 환경 API
├── demographics/  # 인구 통계 API
├── cctv/          # CCTV API
├── route/         # 경로 탐색 API
├── notice/        # 공지 조회 API
├── board/         # 게시판 API
├── qna/           # QnA API
├── report/        # 배치 리포트 조회 API
└── global/        # 공통 응답, 예외, 설정
```

## 리소스

- 설정: `src/main/resources/application.yml`
- 로컬 Docker 설정: `src/main/resources/application-local.yml`
- DB 마이그레이션: `src/main/resources/db/migration/`
- MyBatis Mapper: `src/main/resources/mapper/`
- 초기 목업 데이터: `src/main/resources/data.sql`
