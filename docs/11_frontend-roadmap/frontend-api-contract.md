# 프론트엔드 API 계약

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-15
- 관련 요구사항: REQ-HOUSE-002, REQ-AUTH-001, REQ-ROUTE-001
- 관련 문서: [vue-transition-plan.md](vue-transition-plan.md), [../05_api/api-spec.md](../05_api/api-spec.md), [../03_ui/screen-list.md](../03_ui/screen-list.md)

---

## 화면별 필요한 API 목록

| 화면 | 필요 API |
|------|----------|
| SCR-MAIN | 없음 또는 GET /api/auth/me |
| SCR-SEARCH | GET /api/houses |
| SCR-RESULT | GET /api/houses |
| SCR-DETAIL | GET /api/houses/{houseId}, GET /api/commercial, GET /api/environment, GET /api/routes |
| SCR-LOGIN | POST /api/auth/login, GET /api/auth/me |
| SCR-SIGNUP | POST /api/members |
| SCR-MYPAGE | GET /api/members/me, PUT /api/members/me, DELETE /api/members/me |
| SCR-FAVORITE | GET /api/favorites, POST /api/favorites, DELETE /api/favorites/{favoriteId} |
| SCR-NOTICE-LIST | GET /api/notices |
| SCR-NOTICE-DETAIL | GET /api/notices/{noticeId} |
| SCR-NOTICE-FORM | POST /api/notices, PUT /api/notices/{noticeId} |

배치 관리자 화면은 향후 관리자 전용 확장 범위이며, 현재 사용자 UI 필수 범위에는 포함하지 않는다.

---

## 프론트가 의존하는 응답 필드

| API | 주요 필드 |
|-----|----------|
| GET /api/houses | `items`, `total`, `page`, `size`, `aptName`, `houseType`, `latestDeal.dealType`, `latestDeal.dealAmount`, `latestDeal.depositAmount`, `latestDeal.monthlyRent` |
| GET /api/houses/{houseId} | `houseId`, `aptName`, `jibun`, `latitude`, `longitude`, `deals[].dealType`, `deals[].dealAmount`, `deals[].depositAmount`, `deals[].monthlyRent` |
| GET /api/auth/me | `isAuthenticated`, `memberId`, `name`, `isAdmin` |
| GET /api/routes | `totalDistance`, `estimatedTime`, `searchedNodeCount`, `path` |

---

## 공통 오류 처리 방식

- 성공 여부는 `success` 필드로 판단한다.
- 실패 시 `error.code`, `error.message`를 우선 사용한다.
- 필드 단위 검증 오류가 있으면 `error.fields[]`를 함께 사용한다.
- 401은 로그인 필요 상태로 처리한다.
- 403은 권한 부족 안내로 처리한다.
- 404는 대상 리소스가 없음을 표시한다.
- 422는 경로 탐색 실패처럼 입력은 유효하지만 결과가 없는 상황으로 처리한다.

---

## 로그인 상태 처리

- 초기 화면 진입 시 `GET /api/auth/me`로 세션 상태를 확인할 수 있다.
- 로그인 성공 후 세션 쿠키를 사용해 후속 API를 호출한다.
- 향후 Vue.js 전환 시에도 JWT가 아닌 세션 기반 인증을 유지한다.
- 교차 출처 환경에서는 credentials 포함 요청과 CORS 설정이 필요하다.

---

## 경로 탐색 패널 동작

- 매물 상세 페이지는 비회원도 접근할 수 있다.
- `GET /api/routes`는 로그인한 사용자만 호출할 수 있다.
- 비회원이 경로 탐색 패널을 사용하려고 하면 로그인 안내 메시지를 보여주거나 로그인 페이지로 이동한다.
- 경로 탐색 성공 시 Kakao Maps 위에 경로를 표시하고, 총 거리와 예상 시간을 함께 보여준다.

---

## 배치 관리자 화면 범위

- 배치 관리자 화면은 향후 관리자 전용 확장 기능이다.
- 현재 사용자 UI 필수 범위에는 포함하지 않는다.
- 다만 백엔드 API 계약은 미리 문서화해 추후 운영 화면 추가 시 재사용한다.
