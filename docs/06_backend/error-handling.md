# 에러 처리

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-14
- 관련 요구사항: 전체
- 관련 문서: [backend-architecture.md](backend-architecture.md), [../05_api/api-overview.md](../05_api/api-overview.md)

---

## 공통 오류 응답 구조

모든 API 오류는 아래 구조로 반환한다.

```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "사용자에게 보여줄 메시지"
  }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| success | boolean | 항상 false |
| error.code | String | 도메인 기반 에러 코드 |
| error.message | String | 사용자 친화적 오류 메시지 (한국어) |

---

## 에러 코드 명명 규칙

형식: `{도메인}_{오류 유형}` — 대문자 스네이크케이스

| 도메인 접두어 | 대상 |
|-------------|------|
| HOUSE_ | 주택 거래 도메인 |
| MEMBER_ | 회원 도메인 |
| AUTH_ | 인증 도메인 |
| FAVORITE_ | 관심 지역 도메인 |
| COMMERCIAL_ | 상권 도메인 |
| ENV_ | 환경 정보 도메인 |
| ROUTE_ | 경로 탐색 도메인 |
| NOTICE_ | 공지사항 도메인 |
| COMMON_ | 공통 오류 |

---

## 도메인별 에러 코드 목록

### 주택 (HOUSE)

| 에러 코드 | HTTP 상태 | 설명 |
|----------|-----------|------|
| HOUSE_NOT_FOUND | 404 | 요청한 houseId의 주택이 없음 |
| HOUSE_INVALID_REGION | 400 | 유효하지 않은 행정구역 코드 |
| HOUSE_COLLECT_FAILED | 500 | 공공 데이터 수집 중 오류 |

### 회원 (MEMBER)

| 에러 코드 | HTTP 상태 | 설명 |
|----------|-----------|------|
| MEMBER_NOT_FOUND | 404 | 해당 회원 없음 |
| MEMBER_DUPLICATE_EMAIL | 409 | 이미 사용 중인 이메일 |
| MEMBER_INVALID_PASSWORD | 400 | 비밀번호 형식 오류 |

### 인증 (AUTH)

| 에러 코드 | HTTP 상태 | 설명 |
|----------|-----------|------|
| AUTH_INVALID_CREDENTIALS | 401 | 이메일 또는 비밀번호 불일치 |
| AUTH_UNAUTHORIZED | 401 | 로그인 필요 |
| AUTH_FORBIDDEN | 403 | 접근 권한 없음 |

### 관심 지역 (FAVORITE)

| 에러 코드 | HTTP 상태 | 설명 |
|----------|-----------|------|
| FAVORITE_NOT_FOUND | 404 | 해당 관심 지역 없음 |
| FAVORITE_DUPLICATE | 409 | 이미 등록된 관심 지역 |
| FAVORITE_FORBIDDEN | 403 | 본인 관심 지역이 아님 |

### 경로 탐색 (ROUTE)

| 에러 코드 | HTTP 상태 | 설명 |
|----------|-----------|------|
| ROUTE_NODE_NOT_FOUND | 404 | 출발지 또는 목적지 주변 노드 없음 |
| ROUTE_NOT_FOUND | 422 | A* 탐색 결과 경로 없음 (그래프 연결 끊김) |
| ROUTE_OUT_OF_RANGE | 400 | 탐색 가능 범위 초과 |

### 공지사항 (NOTICE)

| 에러 코드 | HTTP 상태 | 설명 |
|----------|-----------|------|
| NOTICE_NOT_FOUND | 404 | 해당 공지사항 없음 |
| NOTICE_FORBIDDEN | 403 | 공지사항 작성·수정·삭제 권한 없음 |

### 공통 (COMMON)

| 에러 코드 | HTTP 상태 | 설명 |
|----------|-----------|------|
| COMMON_INVALID_INPUT | 400 | 일반 입력 유효성 오류 |
| COMMON_INTERNAL_ERROR | 500 | 서버 내부 오류 |

---

## 유효성 검사 오류 처리

요청 본문 유효성 검사 실패 시 필드별 오류 목록을 반환한다.

```json
{
  "success": false,
  "error": {
    "code": "COMMON_INVALID_INPUT",
    "message": "입력값이 올바르지 않습니다",
    "fields": [
      { "field": "email", "message": "이메일 형식이 올바르지 않습니다" },
      { "field": "password", "message": "비밀번호는 8자 이상이어야 합니다" }
    ]
  }
}
```

Spring의 `@Valid` + `MethodArgumentNotValidException` 처리를 `GlobalExceptionHandler`에서 담당한다.

---

## 외부 API 오류 처리

외부 공공 데이터 API 장애 시 처리 방식:

| 상황 | 처리 방법 |
|------|-----------|
| 국토부 API 타임아웃 | 기 수집 DB 데이터 반환. 수집 실패 로그 기록 |
| 상권 API 오류 | 빈 목록 반환 + "현재 상권 정보를 제공할 수 없습니다" 메시지 |
| 환경 API 오류 | 빈 목록 반환 + "현재 환경 정보를 제공할 수 없습니다" 메시지 |

외부 API 오류는 HTTP 502/503으로 사용자에게 노출하지 않는다. 서비스 내부에서 graceful degradation 처리한다.

---

## DB 오류 처리

| 상황 | 처리 방법 |
|------|-----------|
| DataIntegrityViolationException | 중복 키 → 409 응답 반환 |
| DataAccessException | 500 응답 + 오류 로그 기록 |
| ConnectException | 500 응답 + "서비스를 일시적으로 사용할 수 없습니다" |

---

## 경로 탐색 오류 처리

A* 탐색에서 발생할 수 있는 오류:

| 상황 | 처리 방법 |
|------|-----------|
| 출발지 주변 노드 없음 | ROUTE_NODE_NOT_FOUND (404) |
| 목적지 주변 노드 없음 | ROUTE_NODE_NOT_FOUND (404) |
| open set이 비어도 목적지 미도달 | ROUTE_NOT_FOUND (422) |
| 탐색 노드 수 한도 초과 (미정) | ROUTE_OUT_OF_RANGE (400) |

---

## GlobalExceptionHandler 구조

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(ResourceNotFoundException ex) { ... }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicate(DuplicateResourceException ex) { ... }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<?>> handleUnauthorized(UnauthorizedException ex) { ... }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<?>> handleForbidden(ForbiddenException ex) { ... }

    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleRouteNotFound(RouteNotFoundException ex) { ... }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException ex) { ... }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneral(Exception ex) { ... }
}
```
