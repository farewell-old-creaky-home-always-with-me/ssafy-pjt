# AI 사용 보고서

- 프로젝트: 잘살아봐라마 (SSAFY HOME)
- 작성일: 2026-06-26
- 작성 목적: 프로젝트 수행 중 사용한 AI 도구와 서비스 내부 AI 기능을 정리한다.

---

## 1. 외부 AI 도구 활용

| 항목 | 내용 |
|------|------|
| 사용 도구 | ChatGPT, Codex |
| 사용 목적 | 요구사항 정리, 문서 초안 작성, 코드 구조 분석, 테스트 실패 원인 분석 |
| 주요 산출물 | 요구사항 문서, 화면설계서, API 명세, 클래스 다이어그램, 제출 문서 |
| 검토 방법 | 실제 코드, API 경로, 테스트 결과와 비교해 잘못된 설명을 수정 |

AI 도구는 문서 작성 속도를 높이고 코드 구조를 빠르게 파악하는 데 사용했다. 단, AI가 만든 문장은 그대로 제출하지 않고 프로젝트 구현 상태에 맞게 수정했다.

---

## 2. Spring AI 적용

| 항목 | 내용 |
|------|------|
| 적용 모듈 | `backend/ai-service` |
| 주요 라이브러리 | Spring AI OpenAI, Spring AI Chroma VectorStore, Spring AI Tika Document Reader |
| 사용 모델 | `gpt-5.4-mini`, `text-embedding-3-small` |
| 주요 설정 | `application.yml`의 `spring.ai.openai`, `spring.ai.vectorstore.chroma` |

AI 기능은 별도 `ai-service` 모듈로 분리했다. `AiConfig`에서 Spring AI `ChatClient`를 Bean으로 등록하고, 채팅·문서 검색·배치 리포트 요약 기능에서 공통으로 사용한다.

---

## 3. RAG 기반 AI 채팅

| 항목 | 내용 |
|------|------|
| 관련 클래스 | `ChatbotService`, `DocumentService`, `ChatbotController` |
| 주요 API | `POST /api/chat`, `POST /api/chat/upload`, `GET /api/chat/search` |
| 저장소 | Chroma VectorStore |
| 지원 문서 | `.txt`, `.md`, `.pdf` |

사용자가 문서를 업로드하면 `DocumentService`가 Tika로 텍스트를 추출하고 `TokenTextSplitter`로 나눈 뒤 VectorStore에 저장한다. 질문이 들어오면 `ChatbotService`가 유사 문서 상위 4개를 검색하고, 관련 문서가 있으면 해당 내용을 프롬프트 context로 넣어 답변한다.

RAG 검색이 실패하거나 관련 문서가 없으면 일반 ChatClient 응답으로 fallback한다. 응답에는 `ragUsed` 값을 포함해 문서 기반 답변 여부를 프론트엔드에서 표시할 수 있게 했다.

---

## 4. Tool Calling 기능

| 항목 | 내용 |
|------|------|
| 관련 클래스 | `ToolCallingService`, `StatsTool`, `HouseSearchTool`, `ToolCallPlanner` |
| 주요 API | `POST /api/ai/tools/chat`, `POST /api/ai/tools/multi` |
| 사용 목적 | 지역 통계 조회와 매물 검색을 AI 응답 흐름에 연결 |

Tool Calling 기능은 사용자의 질문에 따라 부동산 통계 도구와 매물 검색 도구를 호출하도록 구성했다. 단순 채팅은 Spring AI `tools(...)` 기능을 사용하고, 다단계 질문은 `ToolCallPlanner`가 필요한 단계를 정한 뒤 통계 조회 결과를 다음 매물 검색 입력으로 넘긴다.

예를 들어 "강남구 평균 거래가를 보고 예산에 맞는 아파트 추천" 같은 질문은 지역 통계 조회 후 검색 조건을 구성하는 흐름으로 처리한다.

---

## 5. 배치 리포트 AI 요약

| 항목 | 내용 |
|------|------|
| 관련 클래스 | `BatchReportSummaryService`, `BatchReportSummaryPromptProvider` |
| 주요 API | `POST /api/ai/batch/reports/summary` |
| 사용 목적 | 배치 수집 결과를 한국어 요약과 영어 번역으로 생성 |

배치 수집 결과에는 수집 건수, 스킵 건수, 실패 건수, 샘플 거래 데이터가 포함된다. `BatchReportSummaryService`는 이 정보를 ChatClient에 전달하고, AI 응답을 `summary`, `translatedSummary` JSON 형식으로 파싱한다.

이 기능은 관리자 배치 리포트에서 수집 결과를 사람이 읽기 쉬운 문장으로 정리하기 위해 사용한다.

---

## 6. 검증 방법

| 검증 대상 | 방법 |
|----------|------|
| RAG 채팅 | 관련 문서가 있을 때 `ragUsed=true`, 없을 때 `false`가 되는지 테스트 |
| 문서 업로드 | 지원 확장자, 빈 파일, 검색 API 동작 테스트 |
| Tool Calling | 도구 호출 응답, 다단계 도구 실행 결과 테스트 |
| 배치 요약 | AI 응답 JSON 파싱, code fence 제거, 필수 필드 검증 테스트 |
| 인증 | AI API에 Authorization 헤더가 없을 때 401 반환 테스트 |

최종 확인은 `./gradlew :ai-service:test`와 `./gradlew test`로 수행했다.

---

## 7. 사용 시 주의 사항

- AI 응답은 외부 모델 결과이므로 중요한 값은 서비스 코드에서 검증한다.
- 문서 업로드 파일은 확장자를 제한하고 빈 파일을 거부한다.
- API 키와 모델 접근 정보는 소스 코드에 직접 작성하지 않는다.
- RAG 검색 결과가 없을 때도 서비스가 중단되지 않도록 일반 채팅으로 fallback한다.
