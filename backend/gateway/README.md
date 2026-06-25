# Gateway

외부 요청을 내부 서비스로 전달하는 API Gateway입니다. 프론트엔드는 기본적으로 Gateway의 `/api/**` 경로를 호출합니다.

## 기술 스택

- Java 17
- Spring Boot 3.5
- Spring Cloud Gateway
- WebFlux
- JWT
- Gradle

## 주요 역할

- 사용자/관리자/AI API 라우팅
- CORS 설정
- JWT 검증 필터
- 서비스별 내부 주소 분리

## 라우팅

| 경로 | 대상 서비스 |
| --- | --- |
| `POST/PUT/DELETE /api/notices/**` | `admin-service` |
| `GET /api/notices/**` | `main-service` |
| `/api/admin/**` | `admin-service` |
| `/api/chat/**` | `ai-service` |
| `/api/**` | `main-service` |

## 실행

백엔드 루트에서 실행합니다.

```bash
cd backend
./gradlew :gateway:bootRun
```

기본 포트: `8080`

Gateway만 단독 실행하려면 라우팅 대상인 `main-service`, `admin-service`, `ai-service`가 먼저 실행되어 있어야 합니다.

## 테스트

```bash
cd backend
./gradlew :gateway:test
```

## 주요 구조

```text
src/main/java/com/ssafy/home/gateway/
├── GatewayApplication.java
├── auth/    # JWT 속성 및 토큰 검증
└── filter/  # 전역 인증 필터
```

## 설정

- 기본 라우팅: `src/main/resources/application.yml`
- Docker Compose 로컬 라우팅: `src/main/resources/application-local.yml`
- 운영 라우팅: `src/main/resources/application-prod.yml`
