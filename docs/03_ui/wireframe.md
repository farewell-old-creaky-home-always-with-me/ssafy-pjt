# 와이어프레임 계획

- 상태: 작성 예정
- 작성자:
- 마지막 수정일: 2026-05-14
- 관련 요구사항: 전체 UI 관련 요구사항
- 관련 문서: [screen-list.md](screen-list.md), [screen-flow.md](screen-flow.md)

---

## 와이어프레임 목적

와이어프레임은 화면 레이아웃, 주요 컴포넌트 배치, 사용자 인터랙션 흐름을 시각적으로 정의하는 저해상도 설계 산출물이다. 개발 시작 전에 팀 내 UI 구조를 합의하고, 백엔드 API 설계와 연계하는 기준으로 사용한다.

---

## 와이어프레임 제작 범위

| 화면 ID | 화면명 | 우선순위 | 상태 |
|---------|--------|----------|------|
| SCR-MAIN | 메인 페이지 | 높음 | 미정 |
| SCR-SEARCH | 주택 검색 페이지 | 높음 | 미정 |
| SCR-RESULT | 검색 결과 페이지 | 높음 | 미정 |
| SCR-DETAIL | 매물 상세 페이지 | 높음 | 미정 |
| SCR-LOGIN | 로그인 페이지 | 높음 | 미정 |
| SCR-SIGNUP | 회원 가입 페이지 | 높음 | 미정 |
| SCR-MYPAGE | 마이페이지 | 중간 | 미정 |
| SCR-FAVORITE | 관심 지역 관리 | 중간 | 미정 |
| SCR-COMMERCIAL | 상권 정보 페이지 | 중간 | 미정 |
| SCR-ENVIRONMENT | 환경 정보 페이지 | 중간 | 미정 |
| SCR-NOTICE-LIST | 공지사항 목록 | 낮음 | 미정 |
| SCR-NOTICE-DETAIL | 공지사항 상세 | 낮음 | 미정 |
| SCR-NOTICE-FORM | 공지사항 작성/수정 | 낮음 | 미정 |

---

## 화면별 레이아웃 요구사항

### SCR-MAIN (메인 페이지)

- 상단: 내비게이션 바 (로고, 메뉴, 로그인 버튼)
- 중앙: 서비스 소개 헤더 + 검색 입력 폼 (시도·시군구 드롭다운 + 검색 버튼)
- 하단: 주요 기능 소개 카드 3개 (검색, 관심 지역, 경로 탐색)
- 최하단: 푸터

### SCR-SEARCH / SCR-RESULT (검색 및 결과)

- 좌측: 검색 조건 패널 (시도·시군구·읍면동 선택, 거래 유형 체크박스, 금액 범위 슬라이더)
- 우측: 검색 결과 목록 (카드 또는 테이블 형식, 페이지네이션)
- 상단: 내비게이션 바

### SCR-DETAIL (매물 상세)

- 상단: 주택 기본 정보 (이름, 주소, 건축연도)
- 중앙 좌: 지도 컴포넌트 (매물 위치 핀)
- 중앙 우: 거래 이력 테이블
- 하단 탭: 상권 정보 / 환경 정보 / 경로 탐색 패널 (탭 전환)
- 경로 탐색 패널: 목적지 입력 필드 + 탐색 버튼 + 결과 표시 영역

### SCR-LOGIN / SCR-SIGNUP (인증)

- 중앙 정렬 카드 레이아웃
- 이메일·비밀번호 입력 필드
- 제출 버튼
- 하단: 반대 기능 링크 (로그인 → 회원 가입, 회원 가입 → 로그인)

### SCR-FAVORITE (관심 지역 관리)

- 상단: 관심 지역 추가 폼 (행정구역 선택 드롭다운)
- 하단: 등록된 관심 지역 목록 (삭제 버튼 포함)

---

## 공통 컴포넌트 명세

| 컴포넌트명 | 설명 | 사용 화면 |
|-----------|------|-----------|
| NavBar | 로고 + 주요 메뉴 + 인증 상태 버튼 | 전체 |
| Footer | 저작권, 링크 | 전체 |
| SearchForm | 시도·시군구·읍면동 드롭다운 선택 | SCR-MAIN, SCR-SEARCH |
| HouseCard | 주택 거래 요약 정보 카드 | SCR-RESULT |
| DealTable | 거래 이력 테이블 | SCR-DETAIL |
| MapViewer | 외부 지도 API 렌더링 영역 | SCR-DETAIL, SCR-COMMERCIAL |
| RoutePanel | 목적지 입력 + 경로 결과 패널 | SCR-DETAIL |
| FavoriteList | 관심 지역 목록 및 삭제 버튼 | SCR-FAVORITE |
| NoticeTable | 공지사항 목록 테이블 | SCR-NOTICE-LIST |
| AlertMessage | 성공/오류/정보 알림 | 전체 |
| LoadingSpinner | API 요청 중 로딩 표시 | 전체 |

---

## 에셋 저장 경로

| 유형 | 경로 |
|------|------|
| 와이어프레임 이미지 | `assets/wireframes/{화면ID}-YYYYMMDD.png` |
| 실행 화면 캡처 | `assets/screenshots/{화면ID}-YYYYMMDD.png` |
| Figma 파일 링크 | 미정 (팀 Figma 공유 링크 확정 후 기재) |

---

## Figma 내보내기 가이드

1. 각 화면을 Figma Frame으로 작성한다. Frame 이름은 화면 ID를 사용한다 (`SCR-MAIN`, `SCR-DETAIL` 등).
2. 와이어프레임은 Low-fidelity로 작성한다. 색상보다 레이아웃 구조에 집중한다.
3. 완성된 Frame을 PNG 1x 해상도로 내보낸다.
4. `assets/wireframes/` 경로에 저장하고, 이 문서의 경로 표를 업데이트한다.
5. 화면 확정 후 High-fidelity 디자인이 완성되면 `assets/screenshots/`에 실행 화면 캡처를 추가한다.
