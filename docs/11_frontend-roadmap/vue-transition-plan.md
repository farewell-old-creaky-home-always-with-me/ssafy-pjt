# Vue 전환 계획

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-14
- 관련 요구사항: REQ-HOUSE-002, REQ-AUTH-001, REQ-ROUTE-001
- 관련 문서: [frontend-api-contract.md](frontend-api-contract.md), [../03_ui/screen-list.md](../03_ui/screen-list.md), [../05_api/api-overview.md](../05_api/api-overview.md)

---

## 현재 프론트엔드 범위

- 현재 구현 범위는 HTML5, CSS3, JavaScript다.
- Fetch API로 백엔드 REST API를 호출한다.
- Kakao Maps API를 사용해 매물 위치와 경로 결과를 표시한다.

---

## Vue.js 전환 목표

- 동일한 백엔드 API를 재사용하는 SPA 구조로 전환한다.
- 화면 ID 체계를 Vue Router 경로와 연결한다.
- 인증 상태, 공통 에러 처리, 로딩 상태를 일관되게 관리한다.

---

## 전환하지 않는 현재 범위

- 현재 단계에서는 Vue.js를 구현하지 않는다.
- 현재 단계에서는 Pinia, Axios, Vite 설정 파일을 만들지 않는다.
- 관리자 배치 화면은 현재 사용자 UI 범위에 포함하지 않는다.

---

## Vue 3 + Vite 선택 이유

- Vue 3는 컴포넌트 기반 구조로 화면 분리가 명확하다.
- Vite는 개발 서버 기동 속도와 번들링 성능이 좋다.
- Vue Router, Pinia와 조합이 단순하다.
- 현재 API-first 백엔드와 자연스럽게 연결된다.

---

## 화면 ID와 Vue Router 경로 대응표

| 화면 ID | Vue Router 경로 |
|---------|-----------------|
| SCR-MAIN | `/` |
| SCR-SEARCH | `/houses/search` |
| SCR-RESULT | `/houses` |
| SCR-DETAIL | `/houses/:houseId` |
| SCR-LOGIN | `/login` |
| SCR-SIGNUP | `/signup` |
| SCR-MYPAGE | `/mypage` |
| SCR-FAVORITE | `/favorites` |
| SCR-COMMERCIAL | `/commercial` |
| SCR-ENVIRONMENT | `/environment` |
| SCR-NOTICE-LIST | `/notices` |
| SCR-NOTICE-DETAIL | `/notices/:noticeId` |
| SCR-NOTICE-FORM | `/admin/notices/form` |

---

## 기존 HTML 화면과 Vue 컴포넌트 대응표

| 현재 화면 | 향후 Vue 컴포넌트 |
|-----------|-------------------|
| 메인 페이지 | `MainPage.vue` |
| 검색/결과 페이지 | `HouseSearchPage.vue`, `HouseResultPage.vue` |
| 매물 상세 페이지 | `HouseDetailPage.vue` |
| 로그인/회원 가입 | `LoginPage.vue`, `SignupPage.vue` |
| 마이페이지 | `MyPage.vue` |
| 관심 지역 | `FavoritePage.vue` |
| 공지사항 | `NoticeListPage.vue`, `NoticeDetailPage.vue`, `NoticeFormPage.vue` |

---

## 단계별 전환 전략

1. 현재 HTML 화면을 API-first 기준으로 정리한다.
2. 공통 API 응답 처리와 인증 체크 규칙을 문서로 고정한다.
3. Vue Router 경로와 화면 ID 매핑을 확정한다.
4. 공통 레이아웃과 인증 상태 관리부터 Vue 컴포넌트로 전환한다.
5. 검색/상세/경로 탐색처럼 API 의존성이 큰 화면부터 순차 전환한다.

---

## 인증, CORS, API 응답 처리 이슈

- 인증은 현재와 동일하게 세션 기반을 유지한다.
- 교차 출처로 분리될 경우 CORS 허용 출처를 명시해야 한다.
- `allowCredentials`를 활성화해야 한다.
- 프론트 요청은 credentials 포함 설정이 필요하다.
- 세션 쿠키의 SameSite 정책을 검토해야 한다.
- 공통 오류 응답은 `success: false`와 `error.code`, `error.message` 기준으로 처리한다.
- 경로 탐색 패널은 로그인 사용자만 허용하며, 비회원은 로그인 유도 메시지 또는 로그인 페이지 이동 처리한다.
- Kakao Maps 스크립트 로드 실패와 API 응답 실패를 분리해 처리해야 한다.
