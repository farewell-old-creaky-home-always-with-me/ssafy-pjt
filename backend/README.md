# 잘살아봐라마 Backend

Spring Boot 3.5 기반 멀티 모듈 백엔드입니다. Gateway가 외부 요청을 받아 사용자 API, 관리자/배치 API, AI API로 라우팅합니다.

## 모듈 구성

| 모듈 | 포트 | 역할 |
| --- | --- | --- |
| `gateway` | 8080 | API Gateway, CORS, JWT 검증, 서비스 라우팅 |
| `main-service` | 8082 | 사용자 API, 회원/인증, 주택 검색, 관심 기능, 게시판, 공지 조회 |
| `admin-service` | 8081 | 관리자 API, 공공 데이터 수집 배치, 공지/QnA 관리 |
| `ai-service` | 8083 | AI 채팅, RAG 문서 검색, 배치 리포트 요약 |

## 기술 스택

- Java 17
- Spring Boot 3.5
- Spring Cloud Gateway
- Spring Security
- MyBatis
- Spring Batch
- Flyway
- MySQL 8
- Spring AI, Chroma
- Gradle

## 사전 준비

- JDK 17 이상
- Docker, Docker Compose
- 외부 API 키
  - 공공데이터포털/국토교통부
  - VWorld
  - 서울 열린데이터광장
  - OpenAI 또는 SSAFY GMS OpenAI 호환 API

민감 정보는 `secret/application-secret.yml`에서 관리합니다.

```bash
mkdir -p secret
cp application-secret.example.yml secret/application-secret.yml
```

## Docker Compose 실행

```bash
docker compose -f docker-compose.local.yml up --build
```

함께 실행되는 주요 인프라:

- MySQL: `localhost:3306`
- Chroma: `localhost:8000`
- Grafana: `localhost:3000`
- Prometheus: `localhost:9090`
- Loki: `localhost:3100`

## 로컬 실행

각 서비스는 개별 `bootRun`으로 실행할 수 있습니다. MySQL, Chroma 등 필요한 외부 인프라는 별도로 실행되어 있어야 합니다.

```bash
./gradlew :main-service:bootRun
./gradlew :admin-service:bootRun
./gradlew :ai-service:bootRun
./gradlew :gateway:bootRun
```

## 테스트 및 빌드

```bash
./gradlew test
./gradlew build
```

특정 모듈만 실행할 수도 있습니다.

```bash
./gradlew :main-service:test
./gradlew :admin-service:test
./gradlew :ai-service:test
./gradlew :gateway:test
```

## 설정 파일

- 공통 설정: 각 모듈의 `src/main/resources/application.yml`
- 로컬 Docker 설정: 각 모듈의 `src/main/resources/application-local.yml`
- 운영 설정: 각 모듈의 `src/main/resources/application-prod.yml`
- 민감 정보: `secret/application-secret.yml`

## 참고

- 사용자 API 상세: `main-service/README.md`
- 관리자/배치 API 상세: `admin-service/README.md`
- AI API 상세: `ai-service/README.md`
- Gateway 상세: `gateway/README.md`
