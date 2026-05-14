# 최종 README 계획

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-14
- 관련 요구사항: 전체
- 관련 문서: [submission-checklist.md](submission-checklist.md)

---

## 목적

이 문서는 프로젝트 루트의 `README.md`에 최종적으로 포함될 내용을 미리 계획한다. 제출 전 이 계획을 기준으로 `README.md`를 작성한다.

---

## 루트 README.md 구성 계획

### 1. 프로젝트 소개

- 서비스명: SSAFY HOME
- 한 줄 설명: 공공 데이터 기반 주택 실거래 정보 검색 및 주거 지원 서비스
- 서비스 목적과 주요 가치를 2~3문장으로 요약
- 대상 사용자 간략 기재

---

### 2. 기술 스택

아래 항목을 뱃지 또는 표 형식으로 기재한다.

| 분류 | 기술 |
|------|------|
| 백엔드 | Java 17, Spring Boot 3.x, MyBatis |
| 데이터베이스 | MySQL 8.x |
| 프론트엔드 | HTML5, CSS3, JavaScript |
| 공공 데이터 | 국토교통부 실거래가 API, VWorld, 서울 열린데이터 |
| 지도 | 미정 (Kakao Map 또는 Naver Map) |
| 도구 | Git, GitHub, Figma, Postman |

---

### 3. 주요 기능

기능별로 간결하게 나열한다.

- 지역(시도·시군구·읍면동) 기반 주택 실거래 검색
- 매물 상세 정보 및 거래 이력 조회
- 회원 가입·로그인·마이페이지
- 관심 지역 등록·관리
- 매물 주변 상권 정보 조회
- 매물 주변 환경 정보 조회 (서울 한정)
- A* 알고리즘 기반 매물 → 목적지 최적 경로 탐색
- 공지사항 조회

---

### 4. 디렉토리 구조

```
ssafy-pjt/
├── src/
│   └── main/
│       ├── java/com/ssafy/home/
│       └── resources/
│           ├── mapper/
│           └── application.yml
├── docs/                   -- 설계 문서 허브
└── README.md
```

---

### 5. 실행 방법

1. 저장소 클론
   ```bash
   git clone https://github.com/whqtker/ssafy-pjt.git
   ```
2. DB 설정: MySQL 8.x 설치 후 `docs/04_database/schema.sql` 실행
3. `application.yml`에서 DB 연결 정보 입력
4. 빌드 및 실행
   ```bash
   ./mvnw spring-boot:run
   # 또는
   ./gradlew bootRun
   ```
5. `http://localhost:8080` 접속

---

### 6. DB 설정

- MySQL 8.x 이상 필요
- `docs/04_database/schema.sql` 실행으로 테이블 생성
- 행정구역 기초 데이터 삽입 방법: (미정, 수집 스크립트 또는 초기 데이터 SQL 파일 경로 기재)
- 공공 데이터 API 키 발급 필요: 공공 데이터 포털, 서울 열린데이터 광장

---

### 7. API 요약

| 도메인 | 주요 엔드포인트 |
|--------|---------------|
| 주택 | GET /api/houses, GET /api/houses/{id} |
| 회원 | POST /api/members, GET /api/members/me |
| 인증 | POST /api/auth/login, POST /api/auth/logout |
| 관심 지역 | GET /api/favorites, POST /api/favorites |
| 상권 | GET /api/commercial |
| 환경 | GET /api/environment |
| 경로 탐색 | GET /api/routes |
| 공지사항 | GET /api/notices, POST /api/notices |

전체 명세: [docs/05_api/api-spec.md](docs/05_api/api-spec.md)

---

### 8. 화면 캡처

실행 화면 캡처 이미지를 아래 위치에 삽입한다.

- 메인 페이지
- 주택 검색 결과 화면
- 매물 상세 화면
- A* 경로 탐색 결과 화면
- 관심 지역 관리 화면

이미지 경로: `docs/assets/screenshots/`

---

### 9. A* 알고리즘 요약

- 매물에서 목적지까지의 최단 경로를 A* 알고리즘으로 탐색한다.
- `route_node`(위치 지점)와 `route_edge`(연결 간선) 그래프를 사전에 DB에 구축한다.
- 휴리스틱: Haversine 직선거리
- 출력: 총 거리, 예상 시간, 경로 노드 목록
- 상세 설명: [docs/07_algorithm/astar-route-planning.md](docs/07_algorithm/astar-route-planning.md)

---

### 10. 팀원 역할

제출 전 작성한다.

| 팀원 | 역할 |
|------|------|
| (미정) | 백엔드 주담당 |
| (미정) | 프론트엔드 주담당 |

---

### 11. 참고 자료

- 국토교통부 실거래가 공개 시스템: https://rt.molit.go.kr
- 공공 데이터 포털: https://www.data.go.kr
- VWorld: https://www.vworld.kr
- 서울 열린데이터 광장: https://data.seoul.go.kr

---

## 작성 시 주의사항

- 루트 `README.md`는 비기술 사용자도 읽을 수 있을 만큼 간결하게 작성한다.
- 실행 방법은 복붙만으로 실행 가능하도록 구체적으로 작성한다.
- 화면 캡처 이미지는 실제 동작하는 화면을 기준으로 삽입한다.
- 민감 정보(API 키, DB 비밀번호)는 절대 포함하지 않는다.
