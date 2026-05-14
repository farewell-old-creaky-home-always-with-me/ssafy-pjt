# 화면 목록

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-14
- 관련 요구사항: REQ-HOUSE-002, REQ-HOUSE-003, REQ-MEMBER-001, REQ-AUTH-001, REQ-AUTH-002, REQ-FAVORITE-001, REQ-COMMERCIAL-001, REQ-ENV-001, REQ-ROUTE-001, REQ-NOTICE-001
- 관련 문서: [screen-flow.md](screen-flow.md), [wireframe.md](wireframe.md), [../01_requirements/functional-requirements.md](../01_requirements/functional-requirements.md)

---

## 화면 목록 표

| 화면 ID | 화면명 | 목적 | 관련 요구사항 | 주요 입력 | 주요 출력 | 관련 API |
|---------|--------|------|--------------|-----------|-----------|----------|
| SCR-MAIN | 메인 페이지 | 서비스 진입점. 검색 기능으로 이동 유도 | REQ-HOUSE-002 | 지역 검색 입력 | 주요 서비스 소개, 검색 폼 | 없음 |
| SCR-SEARCH | 주택 검색 페이지 | 시도·시군구·읍면동 조건으로 주택 거래 검색 | REQ-HOUSE-002, REQ-HOUSE-004, REQ-HOUSE-005 | 시도, 시군구, 읍면동, 거래 유형, 금액 범위 | 검색 조건 입력 폼 | GET /api/houses |
| SCR-RESULT | 주택 검색 결과 페이지 | 검색 조건에 맞는 주택 거래 목록 표시 | REQ-HOUSE-002, REQ-HOUSE-004, REQ-HOUSE-005 | 정렬 기준 선택 | 거래 목록 (주택명, 면적, 거래금액, 거래일) | GET /api/houses |
| SCR-DETAIL | 매물 상세 페이지 | 특정 주택 상세 정보 및 주변 정보 표시 | REQ-HOUSE-003, REQ-COMMERCIAL-001, REQ-ENV-001, REQ-ROUTE-001 | 없음 (URL 파라미터로 houseId) | 주택 정보, 거래 이력, 지도 위치, 상권/환경/경로 탭 | GET /api/houses/{houseId}, GET /api/commercial, GET /api/environment, GET /api/routes |
| SCR-LOGIN | 로그인 페이지 | 이메일·비밀번호로 로그인 | REQ-AUTH-001 | 이메일, 비밀번호 | 로그인 결과 (성공 시 메인 이동) | POST /api/auth/login |
| SCR-SIGNUP | 회원 가입 페이지 | 새 회원 등록 | REQ-MEMBER-001 | 이메일, 비밀번호, 이름 | 가입 완료 메시지 | POST /api/members |
| SCR-MYPAGE | 마이페이지 | 회원 정보 조회·수정·탈퇴 | REQ-MEMBER-002, REQ-MEMBER-003, REQ-MEMBER-004 | 이름, 비밀번호 (수정 시) | 현재 회원 정보, 수정 폼 | GET /api/members/me, PUT /api/members/me, DELETE /api/members/me |
| SCR-FAVORITE | 관심 지역 관리 페이지 | 관심 지역 목록 조회 및 등록·삭제 | REQ-FAVORITE-001, REQ-FAVORITE-002, REQ-FAVORITE-003 | 행정구역 선택 (등록 시) | 관심 지역 목록 | GET /api/favorites, POST /api/favorites, DELETE /api/favorites/{favoriteId} |
| SCR-COMMERCIAL | 상권 정보 페이지 | 위치 주변 상업 시설 조회 및 필터 | REQ-COMMERCIAL-001, REQ-COMMERCIAL-002 | 위도, 경도, 업종 필터 | 상업 시설 목록 (업종, 상호명, 거리) | GET /api/commercial |
| SCR-ENVIRONMENT | 환경 정보 페이지 | 위치 주변 환경 점검 데이터 조회 | REQ-ENV-001 | 위도, 경도 | 환경 정보 목록 (항목, 수치, 점검일) | GET /api/environment |
| SCR-NOTICE-LIST | 공지사항 목록 페이지 | 공지사항 목록 조회 및 관리자 작성 진입 | REQ-NOTICE-001, REQ-NOTICE-005 | 없음 | 공지사항 목록 (제목, 작성일) | GET /api/notices |
| SCR-NOTICE-DETAIL | 공지사항 상세 페이지 | 특정 공지사항 상세 내용 조회 | REQ-NOTICE-002 | 없음 | 제목, 내용, 작성자, 작성일 | GET /api/notices/{noticeId} |
| SCR-NOTICE-FORM | 공지사항 작성/수정 페이지 | 관리자가 공지사항을 작성하거나 수정 | REQ-NOTICE-003, REQ-NOTICE-004 | 제목, 내용 | 저장 결과 | POST /api/notices, PUT /api/notices/{noticeId} |

---

## SCR-DETAIL 경로 탐색 접근 규칙

- 매물 상세 페이지는 비회원도 접근할 수 있다.
- 단, 경로 탐색 패널은 로그인한 사용자만 사용할 수 있다.
- 비회원이 경로 탐색을 시도하면 로그인 안내 메시지를 표시하거나 로그인 페이지로 이동한다.

---

## 공통 컴포넌트

| 컴포넌트 | 적용 화면 | 설명 |
|---------|-----------|------|
| 내비게이션 바 | 전체 | 로고, 메뉴 링크, 로그인/로그아웃 버튼, 사용자 이름 표시 |
| 푸터 | 전체 | 서비스 소개, 저작권 표시 |
| 지도 컴포넌트 | SCR-DETAIL, SCR-COMMERCIAL | 외부 지도 API 연동 위치 표시 |
| 알림 메시지 | 전체 | 성공/오류/경고 메시지 공통 표시 영역 |
| 로딩 인디케이터 | 전체 | API 응답 대기 중 표시 |

---

## 화면 접근 권한

| 화면 ID | 비회원 | 회원 | 관리자 |
|---------|--------|------|--------|
| SCR-MAIN | O | O | O |
| SCR-SEARCH | O | O | O |
| SCR-RESULT | O | O | O |
| SCR-DETAIL | O | O | O |
| SCR-LOGIN | O | X (이미 로그인) | X |
| SCR-SIGNUP | O | X | X |
| SCR-MYPAGE | X | O | O |
| SCR-FAVORITE | X | O | O |
| SCR-COMMERCIAL | O | O | O |
| SCR-ENVIRONMENT | O | O | O |
| SCR-NOTICE-LIST | O | O | O |
| SCR-NOTICE-DETAIL | O | O | O |
| SCR-NOTICE-FORM | X | X | O |
