# 잘살아봐라마 Frontend

사용자용 Vue 웹 애플리케이션입니다. 주택 검색, 관심 기능, 생활 정보, 게시판/QnA, AI 채팅 화면을 제공합니다.

## 기술 스택

- Vue 3
- Vite
- Pinia
- Vue Router
- Axios
- Tailwind CSS
- lucide-vue-next

## 주요 화면

- 홈: `/`
- 주택 검색: `/search`
- 관심 목록: `/favorites`
- 상권 정보: `/commercial`
- 환경 정보: `/environment`
- 공지사항: `/notices`
- 게시판: `/boards`
- QnA: `/qnas`
- 배치 리포트: `/reports/batch`
- AI 채팅: `/chat`
- 로그인/회원가입/마이페이지

## 실행

```bash
npm install
npm run dev
```

기본 주소: `http://localhost:5173`

Vite 개발 서버는 `/api` 요청을 `http://localhost:8080` Gateway로 프록시합니다.

## 빌드

```bash
npm run build
npm run preview
```

## 환경 변수

Vite 환경 파일은 `secret/` 디렉터리에서 읽습니다.

```text
frontend/secret/.env
```

필요한 경우 다음 값을 설정합니다.

```bash
VITE_API_BASE_URL=
```

값을 비워 두면 개발 서버의 `/api` 프록시를 사용합니다.

## 주요 구조

```text
src/
├── api/         # API 클라이언트
├── components/  # 공통 컴포넌트
├── features/    # 도메인별 페이지/컴포넌트
├── router/      # Vue Router 설정
├── stores/      # Pinia 상태 관리
├── utils/       # 유틸리티
├── App.vue
└── main.js
```
