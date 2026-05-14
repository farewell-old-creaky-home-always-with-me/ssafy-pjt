# 패키지 구조

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-14
- 관련 요구사항: 전체
- 관련 문서: [backend-architecture.md](backend-architecture.md), [class-diagram.md](class-diagram.md)

---

## 제안 패키지 구조

```
com.ssafy.home
│
├── SsafyHomeApplication.java          -- Spring Boot 진입점
│
├── member/                            -- 회원 관리 도메인
│   ├── controller/
│   │   └── MemberController.java
│   ├── service/
│   │   └── MemberService.java
│   ├── mapper/
│   │   └── MemberMapper.java
│   └── dto/
│       ├── MemberRequest.java
│       ├── MemberResponse.java
│       └── MemberDto.java
│
├── auth/                              -- 인증/인가 도메인
│   ├── controller/
│   │   └── AuthController.java
│   ├── service/
│   │   └── AuthService.java
│   └── dto/
│       ├── LoginRequest.java
│       └── LoginResponse.java
│
├── house/                             -- 주택 거래 정보 도메인
│   ├── controller/
│   │   └── HouseController.java
│   ├── service/
│   │   ├── HouseService.java
│   │   └── HouseCollectService.java   -- 공공 데이터 수집
│   ├── mapper/
│   │   └── HouseMapper.java
│   ├── client/
│   │   └── MolitApiClient.java        -- 국토교통부 API 클라이언트
│   └── dto/
│       ├── HouseSearchRequest.java
│       ├── HouseListResponse.java
│       └── HouseDetailResponse.java
│
├── favorite/                          -- 관심 지역 도메인
│   ├── controller/
│   │   └── FavoriteController.java
│   ├── service/
│   │   └── FavoriteService.java
│   ├── mapper/
│   │   └── FavoriteMapper.java
│   └── dto/
│       ├── FavoriteRequest.java
│       └── FavoriteResponse.java
│
├── commercial/                        -- 상권 정보 도메인
│   ├── controller/
│   │   └── CommercialController.java
│   ├── service/
│   │   └── CommercialService.java
│   ├── mapper/
│   │   └── CommercialMapper.java
│   ├── client/
│   │   └── CommercialApiClient.java   -- 공공 상권 API 클라이언트
│   └── dto/
│       ├── CommercialSearchRequest.java
│       └── CommercialResponse.java
│
├── environment/                       -- 환경 정보 도메인
│   ├── controller/
│   │   └── EnvironmentController.java
│   ├── service/
│   │   └── EnvironmentService.java
│   ├── mapper/
│   │   └── EnvironmentMapper.java
│   ├── client/
│   │   └── SeoulOpenApiClient.java    -- 서울 열린데이터 클라이언트
│   └── dto/
│       ├── EnvironmentSearchRequest.java
│       └── EnvironmentResponse.java
│
├── route/                             -- 경로 탐색 도메인 (REQ-ROUTE-001)
│   ├── controller/
│   │   └── RouteController.java
│   ├── service/
│   │   └── RouteService.java
│   ├── mapper/
│   │   └── RouteMapper.java
│   ├── algorithm/
│   │   └── AStarPathFinder.java       -- A* 알고리즘 구현
│   ├── domain/
│   │   ├── RouteNode.java             -- 그래프 노드
│   │   ├── RouteEdge.java             -- 그래프 엣지
│   │   └── RouteResult.java           -- 탐색 결과
│   └── dto/
│       ├── RouteRequest.java
│       └── RouteResponse.java
│
├── notice/                            -- 공지사항 도메인
│   ├── controller/
│   │   └── NoticeController.java
│   ├── service/
│   │   └── NoticeService.java
│   ├── mapper/
│   │   └── NoticeMapper.java
│   └── dto/
│       ├── NoticeRequest.java
│       └── NoticeResponse.java
│
└── global/                            -- 공통 설정 및 유틸리티
    ├── config/
    │   ├── MyBatisConfig.java
    │   └── WebMvcConfig.java
    ├── interceptor/
    │   └── AuthInterceptor.java        -- 인증 인터셉터
    ├── exception/
    │   ├── GlobalExceptionHandler.java
    │   ├── ResourceNotFoundException.java
    │   ├── DuplicateResourceException.java
    │   ├── UnauthorizedException.java
    │   ├── ForbiddenException.java
    │   ├── ValidationException.java
    │   └── RouteNotFoundException.java
    └── response/
        ├── ApiResponse.java            -- 공통 응답 래퍼
        └── ErrorDetail.java
```

---

## 패키지별 책임

| 패키지 | 책임 |
|--------|------|
| `member` | 회원 가입, 조회, 수정, 탈퇴 처리 |
| `auth` | 로그인, 로그아웃, 세션 기반 인증 처리 |
| `house` | 주택 거래 목록 검색, 상세 조회, 공공 데이터 수집 |
| `favorite` | 관심 지역 등록·조회·삭제 |
| `commercial` | 위치 기반 주변 상권 정보 제공 |
| `environment` | 위치 기반 주변 환경 정보 제공 |
| `route` | A* 알고리즘 경로 탐색. RouteNode·RouteEdge 그래프 관리 |
| `notice` | 공지사항 CRUD |
| `global` | 공통 응답, 예외 처리, 인증 인터셉터, MyBatis 설정 |

---

## MyBatis Mapper XML 위치

```
src/main/resources/
├── mapper/
│   ├── MemberMapper.xml
│   ├── HouseMapper.xml
│   ├── FavoriteMapper.xml
│   ├── CommercialMapper.xml
│   ├── EnvironmentMapper.xml
│   ├── RouteMapper.xml
│   └── NoticeMapper.xml
└── application.yml
```
