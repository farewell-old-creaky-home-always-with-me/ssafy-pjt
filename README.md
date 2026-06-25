# 잘살아봐라마

공공 데이터 기반 주거 의사결정 지원 서비스입니다. 주택 실거래가 검색, 관심 지역 관리, 주변 상권/환경 정보, 공지사항, 관리자 배치, AI 채팅/RAG 기능을 제공합니다.

## 주요 기능

- 주택 매매/전월세 실거래가 검색
- 지역, 매물, 주변 상권/환경 정보 조회
- 회원 가입, 로그인, 관심 지역/장소 관리
- 관리자용 공공 데이터 수집 및 배치 관리
- 문서 기반 AI 채팅 및 RAG 검색

## 기술 스택

| 영역 | 기술 |
| --- | --- |
| Backend | Java 17, Spring Boot 3.5, Spring Cloud Gateway, MyBatis, Spring Batch, Spring Security |
| Frontend | Vue 3, Vite, Pinia, Vue Router, Axios |
| AI | Spring AI, OpenAI API, Chroma |
| Database | MySQL 8, Flyway |
| Infra/Observability | Docker Compose, Prometheus, Grafana, Loki, Alloy |
| Build | Gradle, npm |

## 프로젝트 구조

```text
.
├── backend/          # Spring Boot 멀티 모듈 백엔드
│   ├── gateway/      # API Gateway
│   ├── main-service/ # 사용자 API
│   ├── admin-service/# 관리자/배치 API
│   └── ai-service/   # AI 채팅/RAG API
├── frontend/         # 사용자 웹
├── frontend-admin/   # 관리자 웹
├── infra/            # 인프라 관련 파일
├── artifact/         # 산출물 문서
└── docs/             # 개발 컨벤션 문서
```

## 사전 준비

- JDK 17 이상
- Node.js 20 이상 권장
- Docker, Docker Compose
- 외부 API 키
  - 국토교통부/공공데이터포털
  - VWorld
  - 서울 열린데이터광장
  - OpenAI 또는 SSAFY GMS OpenAI 호환 API

백엔드는 민감 정보를 `backend/secret/application-secret.yml`에서 읽습니다. 예시는 `backend/application-secret.example.yml`을 참고하세요.

```bash
mkdir -p backend/secret
cp backend/application-secret.example.yml backend/secret/application-secret.yml
```

## 실행 방법

### 1. 백엔드 및 인프라 실행

```bash
cd backend
docker compose -f docker-compose.local.yml up --build
```

기본 포트는 다음과 같습니다.

| 서비스 | URL |
| --- | --- |
| Gateway | `http://localhost:8080` |
| Admin Service | `http://localhost:8081` |
| Main Service | `http://localhost:8082` |
| AI Service | `http://localhost:8083` |
| Chroma | `http://localhost:8000` |
| Grafana | `http://localhost:3000` |
| Prometheus | `http://localhost:9090` |

### 2. 사용자 프론트엔드 실행

```bash
cd frontend
npm install
npm run dev
```

기본 주소: `http://localhost:5173`

### 3. 관리자 프론트엔드 실행

```bash
cd frontend-admin
npm install
npm run dev
```

기본 주소: `http://localhost:5174`

## 로컬 개발 명령어

### Backend

```bash
cd backend
./gradlew build
./gradlew test
./gradlew :main-service:bootRun
./gradlew :admin-service:bootRun
./gradlew :ai-service:bootRun
./gradlew :gateway:bootRun
```

### Frontend

```bash
cd frontend
npm run build
npm run preview
```

### Admin Frontend

```bash
cd frontend-admin
npm run build
npm run preview
```

## 참고 문서

- 백엔드 상세 문서: `backend/README.md`
- 개발 컨벤션: `docs/conventions/`
- 산출물 문서: `artifact/docs/`
