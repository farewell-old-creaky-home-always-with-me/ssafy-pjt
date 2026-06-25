# AI Service

AI 채팅과 문서 기반 RAG 검색을 담당하는 서비스입니다. Chroma 벡터 스토어와 OpenAI 호환 API를 사용해 사용자 질문에 답변하고, 배치 리포트 요약 기능을 제공합니다.

## 기술 스택

- Java 17
- Spring Boot 3.5
- Spring MVC
- Spring Security
- Spring AI
- OpenAI 호환 API
- Chroma Vector Store
- Apache Tika Document Reader
- SpringDoc OpenAPI
- Gradle

## 주요 기능

- AI 채팅 API
- 문서 업로드 및 청킹
- Chroma 기반 벡터 검색
- RAG 응답 생성
- 배치 리포트 요약
- 도구 호출 기반 질의 처리

## 실행

백엔드 루트에서 실행합니다.

```bash
cd backend
./gradlew :ai-service:bootRun
```

기본 포트: `8083`

Gateway를 함께 실행하면 채팅 API는 `http://localhost:8080/api/chat/**` 경로로 접근합니다.

## 테스트

```bash
cd backend
./gradlew :ai-service:test
```

## 주요 구조

```text
src/main/java/com/ssafy/home/
├── chatbot/      # 채팅, 문서 처리, RAG 서비스
├── batchreport/  # 배치 리포트 요약 API
├── toolcalling/  # 도구 호출 기반 처리
└── global/       # 공통 응답, 예외, 설정
```

## 설정

주요 설정은 `src/main/resources/application.yml`에 있습니다.

- OpenAI 호환 API base URL/model
- Chroma host, port, collection
- 업로드 파일 크기 제한
- JWT 설정

로컬 Docker Compose 실행 시 `application-local.yml`에서 Chroma 주소를 `http://chroma:8000`으로 덮어씁니다.
