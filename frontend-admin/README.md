# 잘살아봐라마 Admin Frontend

관리자용 Vue 웹 애플리케이션입니다. 관리자 로그인 후 공공 데이터 수집 배치와 관련 작업을 실행/확인하는 화면을 제공합니다.

## 기술 스택

- Vue 3
- Vite
- Pinia
- Vue Router
- Axios
- lucide-vue-next

## 주요 화면

- 관리자 로그인: `/login`
- 배치 관리: `/`

## 실행

```bash
npm install
npm run dev
```

기본 주소: `http://localhost:5174`

Vite 개발 서버는 `/api` 요청을 `http://localhost:8080` Gateway로 프록시합니다.

## 빌드

```bash
npm run build
npm run preview
```

## 환경 변수

필요한 경우 Vite 환경 변수로 API base URL을 지정합니다.

```bash
VITE_API_BASE_URL=
```

값을 비워 두면 개발 서버의 `/api` 프록시를 사용합니다.

## 주요 구조

```text
src/
├── api/       # 관리자 API 클라이언트
├── features/  # 로그인/배치 화면
├── router/    # 관리자 라우터 및 인증 가드
├── stores/    # 관리자 인증 상태
├── App.vue
└── main.js
```
