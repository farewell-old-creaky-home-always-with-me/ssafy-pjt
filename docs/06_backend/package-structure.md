# 패키지 구조

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-14
- 관련 요구사항: 전체
- 관련 문서: [backend-architecture.md](backend-architecture.md), [class-diagram.md](class-diagram.md)

---

## 제안 패키지 구조

```text
com.ssafy.home
│
├── SsafyHomeApplication.java
│
├── member/
├── auth/
├── house/
│   ├── controller/
│   ├── service/
│   ├── mapper/
│   └── dto/
│
├── admin/
│   ├── controller/
│   │   └── AdminBatchController.java
│   └── service/
│       └── BatchJobService.java
│
├── batch/
│   ├── config/
│   ├── job/
│   ├── step/
│   ├── reader/
│   ├── processor/
│   ├── writer/
│   ├── listener/
│   └── mapper/
│
├── external/
│   ├── molit/
│   ├── vworld/
│   └── seoul/
│
├── favorite/
├── commercial/
├── environment/
├── route/
│   ├── controller/
│   ├── service/
│   ├── mapper/
│   ├── algorithm/
│   ├── domain/
│   └── dto/
│
├── notice/
└── global/
    ├── config/
    ├── interceptor/
    ├── exception/
    └── response/
```

---

## 배치 및 외부 연동 패키지 상세

```text
com.ssafy.home.batch
 ├── config      -- Spring Batch 설정
 ├── job         -- Job 정의
 ├── step        -- Step 정의
 ├── reader      -- 공공 데이터 Reader
 ├── processor   -- 검증·정규화
 ├── writer      -- DB Writer
 └── listener    -- Job/Step 로그, 수집 로그 기록

com.ssafy.home.external
 ├── molit       -- 국토교통부 API 클라이언트와 응답 파서
 ├── vworld      -- VWorld 행정구역 API 클라이언트
 └── seoul       -- 서울 열린데이터 API 클라이언트
```

---

## 패키지별 책임

| 패키지 | 책임 |
|--------|------|
| `member` | 회원 가입, 조회, 수정, 탈퇴 처리 |
| `auth` | 로그인, 로그아웃, 세션 기반 인증 처리 |
| `house` | 주택 거래 목록 검색, 상세 조회 |
| `admin` | 관리자용 배치 실행 API, 배치 운영 조회 API |
| `batch.config` | Spring Batch 설정, JobLauncher, Job 등록 |
| `batch.job` | Job 정의 (`houseDealCollectJob`) |
| `batch.step` | Step 정의 |
| `batch.reader` | 공공 데이터 Reader |
| `batch.processor` | 데이터 검증·정규화 |
| `batch.writer` | `house`, `house_deal` 적재 |
| `batch.listener` | Job/Step 로그 기록, `batch_collection_log` 저장 |
| `batch.mapper` | 배치 로그 테이블 접근 |
| `external.molit` | 국토교통부 API 클라이언트와 응답 파서 |
| `external.vworld` | VWorld 행정구역 API 클라이언트 |
| `external.seoul` | 서울 열린데이터 API 클라이언트 |
| `favorite` | 관심 지역 등록·조회·삭제 |
| `commercial` | 위치 기반 주변 상권 정보 제공 |
| `environment` | 위치 기반 주변 환경 정보 제공 |
| `route` | A* 알고리즘 경로 탐색. 초기 구현은 전체 RouteNode·RouteEdge 그래프 메모리 로드 후 탐색 |
| `notice` | 공지사항 CRUD |
| `global` | 공통 응답, 예외 처리, 인증 인터셉터, MyBatis 설정 |

---

## MyBatis Mapper XML 위치

```text
src/main/resources/
├── mapper/
│   ├── MemberMapper.xml
│   ├── HouseMapper.xml
│   ├── BatchCollectionLogMapper.xml
│   ├── FavoriteMapper.xml
│   ├── CommercialMapper.xml
│   ├── EnvironmentMapper.xml
│   ├── RouteMapper.xml
│   └── NoticeMapper.xml
└── application.yml
```

---

## 패키지 설계 메모

- 배치 수집 로직은 `house.service`에 두지 않고 `batch`와 `external`로 분리한다.
- `RouteMapper`의 초기 구현 메서드는 `findAllNodes()`, `findAllEdges()`, `findNearestNode(lat, lng)`다.
- `findEdgesByFromNodeId(fromNodeId)`는 그래프 규모 확장 시 사용할 향후 최적화 메서드로 분리한다.
