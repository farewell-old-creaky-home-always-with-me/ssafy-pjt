# API 설계 원칙

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-15
- 관련 요구사항: 전체
- 관련 문서: [api-spec.md](api-spec.md), [../06_backend/backend-architecture.md](../06_backend/backend-architecture.md)

---

## API 설계 원칙

1. **REST 원칙 준수**: 리소스 중심으로 URL을 설계한다. 행위는 HTTP 메서드로 표현한다.
2. **명사 복수형**: URL 경로에는 복수 명사를 소문자로 사용한다. 예: `/api/houses`, `/api/members`
3. **버전 없음**: 초기 버전은 URL에 버전을 포함하지 않는다. `/api/v1/` 형식은 사용하지 않는다.
4. **단일 책임**: 하나의 엔드포인트는 하나의 작업을 수행한다.
5. **공통 응답 포맷**: 성공과 오류 모두 동일한 JSON 구조로 반환한다.
6. **API-first 설계**: 현재 HTML/CSS/JavaScript 화면과 향후 Vue.js 프론트엔드가 동일한 REST API를 재사용할 수 있도록 설계한다.

---

## 기본 URL

```
개발 환경: http://localhost:8080
운영 환경: 미정
```

모든 API 경로는 `/api` 접두사로 시작한다.

---

## 공통 성공 응답 포맷

```json
{
  "success": true,
  "data": { /* 실제 응답 데이터 */ },
  "message": "요청이 처리되었습니다"
}
```

`message`는 선택 필드이며, 별도 안내 문구가 필요하지 않은 성공 응답에서는 생략할 수 있다.

목록 응답의 경우:

```json
{
  "success": true,
  "data": {
    "items": [ /* 목록 데이터 */ ],
    "total": 100,
    "page": 1,
    "size": 20
  },
  "message": "요청이 처리되었습니다"
}
```

목록 응답도 동일하게 `message`를 선택적으로 포함한다.

---

## 공통 오류 응답 포맷

```json
{
  "success": false,
  "error": {
    "code": "HOUSE_NOT_FOUND",
    "message": "해당 주택 정보를 찾을 수 없습니다"
  }
}
```

필드 유효성 검사 오류처럼 세부 오류가 필요한 경우에만 `error.fields`를 선택적으로 포함한다.

---

## HTTP 상태 코드 사용

| 상태 코드 | 사용 상황 |
|-----------|-----------|
| 200 OK | 조회, 수정 성공 |
| 201 Created | 신규 리소스 생성 성공 |
| 204 No Content | 삭제 성공 (응답 본문 없음) |
| 400 Bad Request | 요청 데이터 유효성 검사 실패 |
| 401 Unauthorized | 인증 필요 (로그인하지 않은 상태) |
| 403 Forbidden | 권한 없음 (로그인은 됐으나 권한 부족) |
| 404 Not Found | 리소스 없음 |
| 409 Conflict | 중복 리소스 (이미 등록된 이메일, 중복 관심 지역 등) |
| 422 Unprocessable Entity | 요청 형식은 유효하지만 처리 결과가 없음 (예: 경로 탐색 실패) |
| 500 Internal Server Error | 서버 내부 오류 |

`204 No Content` 응답은 공통 응답 포맷을 적용하지 않으며, 응답 본문을 반환하지 않는다.

---

## 인증/세션 방식

- **세션 기반 인증**을 사용한다.
- 로그인 성공 시 서버에서 세션을 생성하고 클라이언트에 세션 쿠키를 발급한다.
- 인증이 필요한 API는 요청 쿠키에서 세션을 검증한다.
- JWT 방식으로 전환하지 않는다. 현재와 향후 Vue.js 전환 이후에도 세션 기반 인증을 유지한다.
- 인증 실패 시 HTTP 401을 반환한다.
- 권한 부족 시 HTTP 403을 반환한다.

향후 Vue.js 전환 시 교차 출처 환경을 고려해 아래 정책을 함께 검토한다.

- CORS 허용 출처(`allowedOrigins`) 명시
- `allowCredentials` 활성화
- 프론트엔드 요청의 credentials 포함 설정
- 세션 쿠키의 SameSite 정책 검토

---

## 명명 규칙

| 항목 | 규칙 | 예시 |
|------|------|------|
| URL 경로 | 소문자 케밥케이스 또는 복수 명사 경로 | `/api/favorites` |
| URL 리소스 | 복수 명사 | `/api/houses`, `/api/members` |
| 쿼리 파라미터 | 카멜케이스 | `?startLat=37.5&endLng=126.9` |
| 요청 본문 필드 | 카멜케이스 | `{"dealAmount": 50000}` |
| 응답 필드 | 카멜케이스 | `{"totalDistance": 1200}` |
| 에러 코드 | 대문자 스네이크케이스 | `HOUSE_NOT_FOUND`, `MEMBER_DUPLICATE_EMAIL` |

---

## 페이지네이션

목록 조회 API에는 페이지네이션 파라미터를 지원한다.

| 파라미터 | 기본값 | 설명 |
|---------|--------|------|
| page | 1 | 페이지 번호 (1부터 시작) |
| size | 20 | 페이지당 항목 수 |

---

## 권한 적용 기준

| 구분 | 설명 | 대표 API |
|------|------|-----------|
| 비회원 접근 가능 | 조회성 기능. 로그인 없이 사용 가능 | `GET /api/houses`, `GET /api/houses/{houseId}`, `GET /api/commercial`, `GET /api/environment`, `GET /api/notices`, `GET /api/notices/{noticeId}` |
| 로그인 필요 | 개인화 정보 또는 세션 사용 기능 | `POST /api/auth/logout`, `GET /api/auth/me`, `GET/PUT/DELETE /api/members/me`, `POST/GET/DELETE /api/favorites`, `GET /api/routes` |
| 관리자만 가능 | 운영 또는 관리 기능 | `POST /api/notices`, `PUT /api/notices/{noticeId}`, `DELETE /api/notices/{noticeId}`, `POST /api/admin/batch/house-deals` |

---

## 외부 공공 데이터 API 오류 처리

외부 API 장애 시 서비스 자체 에러로 전파하지 않는다. 기 수집 DB 데이터를 우선 반환하고, 실시간 조회가 불가한 경우 오류 메시지와 함께 빈 데이터를 반환한다.
