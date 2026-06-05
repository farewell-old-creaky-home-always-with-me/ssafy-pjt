# SSAFY HOME Frontend

공공 데이터 기반 주택 실거래 정보 웹 서비스 정적 프로토타입

---

## 기술 스택

| 항목 | 내용 |
|------|------|
| Language | HTML5, CSS3, JavaScript (ES6+) |
| Map | Kakao Maps API |
| Build Tool | 없음 (정적 파일) |

---

## 사전 요구사항

- 웹 브라우저 (Chrome 권장)
- 백엔드 서버 실행 중 (`http://localhost:8080`)
- Kakao Maps API 키 (지도 기능 사용 시)

---

## 환경 설정

지도 기능을 사용하려면 Kakao Maps API 키를 각 HTML 파일의 SDK 스크립트 태그에 설정한다.

```html
<!-- search.html, commercial.html, environment.html -->
<script src="//dapi.kakao.com/v2/maps/sdk.js?appkey={YOUR_KAKAO_APP_KEY}&libraries=services"></script>
```

---

## 실행 방법

```bash
# 브라우저에서 직접 열기
open index.html

# 또는 로컬 HTTP 서버 실행 (지도 등 HTTP 컨텍스트가 필요한 경우)
python3 -m http.server 5500
# → http://localhost:5500
```

---

## 페이지 구조

```
frontend/
├── index.html             # 메인 홈
├── login.html             # 로그인
├── signup.html            # 회원 가입
├── password-recovery.html # 비밀번호 찾기
├── search.html            # 주택 거래 검색·지도
├── commercial.html        # 주변 상권 조회
├── environment.html       # 주변 환경 조회
├── favorites.html         # 관심 지역 관리
├── notices.html           # 공지사항 목록
├── profile.html           # 마이페이지
├── css/
│   ├── reset.css          # 브라우저 초기화
│   ├── variables.css      # 디자인 토큰
│   ├── base.css           # 공통 기본 스타일
│   ├── layout.css         # 레이아웃
│   ├── components.css     # 공통 컴포넌트
│   └── pages/             # 페이지별 스타일
└── js/
    ├── shared.js          # 공통 유틸 (네비게이션, 인증 상태)
    ├── user-store.js      # 사용자 세션 상태 관리
    ├── favorites-store.js # 관심 지역 상태 관리
    ├── search.js          # 주택 검색·지도 로직
    └── commercial.js      # 상권 조회·지도 로직
```
