# ERD

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-14
- 관련 요구사항: REQ-HOUSE-001, REQ-HOUSE-002, REQ-HOUSE-003, REQ-MEMBER-001, REQ-FAVORITE-001, REQ-COMMERCIAL-001, REQ-ENV-001, REQ-ROUTE-001, REQ-NOTICE-001
- 관련 문서: [table-spec.md](table-spec.md), [schema.sql](schema.sql), [data-source.md](data-source.md)

---

## 주요 엔티티 목록

| 엔티티 | 테이블명 | 설명 |
|--------|----------|------|
| 행정구역 | region_code | 시도·시군구·읍면동 코드 체계 |
| 주택 | house | 아파트·다세대 단지 기본 정보 |
| 주택 거래 | house_deal | 주택별 실거래 이력 |
| 회원 | member | 서비스 사용자 계정 |
| 관심 지역 | favorite_area | 회원이 등록한 관심 행정구역 |
| 상권 정보 | commercial_area | 위치 주변 상업 시설 정보 |
| 환경 정보 | environment_info | 위치 주변 환경 점검 데이터 |
| 배치 수집 로그 | batch_collection_log | 배치 실행별 수집 요약 결과 |
| 공지사항 | notice | 서비스 공지 정보 |
| 경로 노드 | route_node | A* 탐색용 그래프 노드 (위치 지점) |
| 경로 엣지 | route_edge | A* 탐색용 그래프 간선 (노드 간 연결) |

---

## 엔티티 관계

| 관계 | 카디널리티 | 설명 |
|------|-----------|------|
| region_code — house | 1 : N | 한 행정구역에 여러 주택 단지 존재 |
| house — house_deal | 1 : N | 한 주택에 여러 거래 이력 존재 |
| member — favorite_area | 1 : N | 한 회원이 여러 관심 지역 등록 가능 |
| region_code — favorite_area | 1 : N | 한 행정구역이 여러 회원의 관심 지역으로 등록 가능 |
| member — notice | 1 : N | 한 회원(관리자)이 여러 공지사항 작성 가능 |
| region_code — batch_collection_log | 1 : N | 한 행정구역 기준으로 여러 수집 로그가 기록될 수 있음 |
| route_node — route_edge | 1 : N (출발) | 한 노드에서 여러 엣지 출발 가능 |
| route_node — route_edge | 1 : N (도착) | 한 노드로 여러 엣지 도착 가능 |

`commercial_area`, `environment_info`는 외부 API에서 조회하는 데이터로, 회원·주택 테이블과 직접 FK 관계를 갖지 않는다. 위도·경도 기반으로 서비스 레이어에서 연계한다.

Spring Batch의 Job/Step 실행 메타데이터 테이블은 프레임워크 기본 `schema-mysql.sql`을 사용해 생성한다. 이 문서에는 서비스 전용 테이블인 `batch_collection_log`만 포함한다.

---

## Mermaid ER 다이어그램

```mermaid
erDiagram
    region_code {
        varchar region_code PK "행정구역 코드 (10자리)"
        varchar sido_name "시도명"
        varchar sigungu_name "시군구명"
        varchar dong_name "읍면동명"
    }

    house {
        bigint house_id PK "주택 ID"
        varchar region_code FK "행정구역 코드"
        varchar apt_name "단지명"
        varchar jibun "지번 주소"
        int build_year "건축연도"
        varchar house_type "주택 유형 (아파트/다세대)"
        decimal latitude "위도"
        decimal longitude "경도"
    }

    house_deal {
        bigint deal_id PK "거래 ID"
        bigint house_id FK "주택 ID"
        varchar deal_type "거래 유형"
        int deal_amount "거래금액 (만원)"
        int deposit_amount "보증금액 (만원)"
        int monthly_rent "월세금액 (만원)"
        date deal_date "거래일"
        decimal area "전용면적 (㎡)"
        int floor "층"
    }

    batch_collection_log {
        bigint collection_log_id PK "수집 로그 ID"
        bigint job_execution_id "Job 실행 ID"
        varchar job_name "Job 이름"
        varchar data_type "수집 데이터 유형"
        varchar region_code FK "행정구역 코드"
        varchar year_month "수집 연월"
        varchar house_type "주택 유형"
        varchar deal_type "거래 유형"
        int collected_count "수집 반영 건수"
        int skipped_count "스킵 건수"
        int failed_count "실패 건수"
        varchar status "실행 상태"
    }

    member {
        bigint member_id PK "회원 ID"
        varchar email "이메일 (unique)"
        varchar password "비밀번호 (해시)"
        varchar name "이름"
        datetime created_at "가입일시"
        tinyint is_admin "관리자 여부"
    }

    favorite_area {
        bigint favorite_id PK "관심 지역 ID"
        bigint member_id FK "회원 ID"
        varchar region_code FK "행정구역 코드"
        datetime created_at "등록일시"
    }

    notice {
        bigint notice_id PK "공지사항 ID"
        bigint member_id FK "작성자 회원 ID"
        varchar title "제목"
        text content "내용"
        datetime created_at "작성일시"
        datetime updated_at "수정일시"
    }

    commercial_area {
        bigint commercial_id PK "상권 ID"
        varchar biz_name "상호명"
        varchar category_large "업종 대분류"
        varchar category_medium "업종 중분류"
        decimal latitude "위도"
        decimal longitude "경도"
        varchar address "주소"
    }

    environment_info {
        bigint env_id PK "환경 정보 ID"
        varchar item_name "점검 항목명"
        decimal value "측정 수치"
        varchar unit "단위"
        date measured_date "측정일"
        decimal latitude "위도"
        decimal longitude "경도"
    }

    route_node {
        bigint node_id PK "노드 ID"
        decimal latitude "위도"
        decimal longitude "경도"
        varchar node_name "노드 명칭 (선택)"
    }

    route_edge {
        bigint edge_id PK "엣지 ID"
        bigint from_node_id FK "출발 노드 ID"
        bigint to_node_id FK "도착 노드 ID"
        double distance "거리 (미터)"
    }

    region_code ||--o{ house : "포함"
    house ||--o{ house_deal : "보유"
    member ||--o{ favorite_area : "등록"
    region_code ||--o{ favorite_area : "지정"
    member ||--o{ notice : "작성"
    region_code ||--o{ batch_collection_log : "기준"
    route_node ||--o{ route_edge : "출발"
    route_node ||--o{ route_edge : "도착"
```

---

## ERD 작성 가이드

1. **문서 기준**: Markdown 내 Mermaid ER 다이어그램을 기준 소스로 유지한다.
2. **저장 위치**: 정제된 이미지는 `assets/diagrams/erd-YYYYMMDD.png`에 저장한다.
3. **표기법**: Crow's Foot 표기법으로 카디널리티를 표시한다.
4. **컬럼 표기**: PK, FK, NOT NULL, 데이터 타입을 명시한다.
5. **확정 전 컬럼**: 미정 항목은 표시하되 주석으로 미정 표기한다.
