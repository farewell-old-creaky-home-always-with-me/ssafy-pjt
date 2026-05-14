# 테이블 명세

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-14
- 관련 요구사항: REQ-HOUSE-001, REQ-HOUSE-002, REQ-MEMBER-001, REQ-AUTH-001, REQ-FAVORITE-001, REQ-COMMERCIAL-001, REQ-ENV-001, REQ-ROUTE-001, REQ-NOTICE-001
- 관련 문서: [erd.md](erd.md), [schema.sql](schema.sql)

---

## region_code (행정구역 코드)

**목적**: 시도·시군구·읍면동 행정구역 코드와 명칭을 관리한다. 주택 검색과 관심 지역의 기준 단위다.

| 컬럼명 | 타입 | PK | FK | NOT NULL | 설명 |
|--------|------|----|----|----------|------|
| region_code | VARCHAR(10) | O | | O | 행정구역 코드 (10자리, VWorld 기준) |
| sido_name | VARCHAR(20) | | | O | 시도명 |
| sigungu_name | VARCHAR(30) | | | | 시군구명 (시도 단위면 NULL 허용) |
| dong_name | VARCHAR(30) | | | | 읍면동명 (시군구 단위면 NULL 허용) |
| created_at | DATETIME | | | O | 데이터 등록일시 |

**인덱스**: PRIMARY KEY (region_code), INDEX (sido_name, sigungu_name)

---

## house (주택 단지)

**목적**: 아파트·다세대 주택 단지의 기본 정보를 저장한다. 행정구역 코드와 연결된다.

| 컬럼명 | 타입 | PK | FK | NOT NULL | 설명 |
|--------|------|----|----|----------|------|
| house_id | BIGINT | O | | O | 주택 ID (AUTO_INCREMENT) |
| region_code | VARCHAR(10) | | O | O | 행정구역 코드 (region_code 참조) |
| apt_name | VARCHAR(100) | | | O | 단지명 |
| jibun | VARCHAR(50) | | | | 지번 주소 |
| road_address | VARCHAR(100) | | | | 도로명 주소 (미정) |
| build_year | INT | | | | 건축연도 |
| house_type | VARCHAR(10) | | | O | 주택 유형 ('아파트', '다세대') |
| latitude | DECIMAL(10,7) | | | | 위도 |
| longitude | DECIMAL(10,7) | | | | 경도 |
| created_at | DATETIME | | | O | 데이터 등록일시 |

**인덱스**: PRIMARY KEY (house_id), INDEX (region_code), INDEX (apt_name), UNIQUE (region_code, apt_name, jibun)

---

## house_deal (주택 거래 이력)

**목적**: 특정 주택의 실거래 1건 정보를 저장한다. 국토교통부 API에서 수집한다.

| 컬럼명 | 타입 | PK | FK | NOT NULL | 설명 |
|--------|------|----|----|----------|------|
| deal_id | BIGINT | O | | O | 거래 ID (AUTO_INCREMENT) |
| house_id | BIGINT | | O | O | 주택 ID (house 참조) |
| deal_amount | INT | | | O | 거래금액 (만 원) |
| deal_date | DATE | | | O | 거래일 (연월일 조합) |
| area | DECIMAL(6,2) | | | O | 전용면적 (㎡) |
| floor | INT | | | | 층수 |
| created_at | DATETIME | | | O | 데이터 등록일시 |

**인덱스**: PRIMARY KEY (deal_id), INDEX (house_id), INDEX (deal_date), INDEX (deal_amount)

---

## member (회원)

**목적**: 서비스 사용자 계정 정보를 저장한다.

| 컬럼명 | 타입 | PK | FK | NOT NULL | 설명 |
|--------|------|----|----|----------|------|
| member_id | BIGINT | O | | O | 회원 ID (AUTO_INCREMENT) |
| email | VARCHAR(100) | | | O | 이메일 (로그인 ID, UNIQUE) |
| password | VARCHAR(255) | | | O | 비밀번호 (bcrypt 해시) |
| name | VARCHAR(50) | | | O | 이름 |
| is_admin | TINYINT(1) | | | O | 관리자 여부 (0: 일반, 1: 관리자) |
| created_at | DATETIME | | | O | 가입일시 |
| updated_at | DATETIME | | | | 정보 수정일시 |

**인덱스**: PRIMARY KEY (member_id), UNIQUE (email)

---

## favorite_area (관심 지역)

**목적**: 회원이 즐겨찾기로 등록한 행정구역을 저장한다.

| 컬럼명 | 타입 | PK | FK | NOT NULL | 설명 |
|--------|------|----|----|----------|------|
| favorite_id | BIGINT | O | | O | 관심 지역 ID (AUTO_INCREMENT) |
| member_id | BIGINT | | O | O | 회원 ID (member 참조) |
| region_code | VARCHAR(10) | | O | O | 행정구역 코드 (region_code 참조) |
| created_at | DATETIME | | | O | 등록일시 |

**인덱스**: PRIMARY KEY (favorite_id), INDEX (member_id), UNIQUE (member_id, region_code)

---

## commercial_area (상권 정보)

**목적**: 위치 주변 상업 시설 정보를 저장한다. 실시간 API 조회 또는 배치 수집 방식은 미정.

| 컬럼명 | 타입 | PK | FK | NOT NULL | 설명 |
|--------|------|----|----|----------|------|
| commercial_id | BIGINT | O | | O | 상권 ID (AUTO_INCREMENT) |
| biz_name | VARCHAR(100) | | | O | 상호명 |
| category_large | VARCHAR(50) | | | | 업종 대분류 |
| category_medium | VARCHAR(50) | | | | 업종 중분류 |
| category_small | VARCHAR(50) | | | | 업종 소분류 |
| latitude | DECIMAL(10,7) | | | O | 위도 |
| longitude | DECIMAL(10,7) | | | O | 경도 |
| address | VARCHAR(150) | | | | 주소 |
| created_at | DATETIME | | | O | 데이터 등록일시 |

**인덱스**: PRIMARY KEY (commercial_id), SPATIAL INDEX 또는 INDEX (latitude, longitude)

---

## environment_info (환경 정보)

**목적**: 서울 열린데이터 기반 환경 점검 데이터를 저장한다.

| 컬럼명 | 타입 | PK | FK | NOT NULL | 설명 |
|--------|------|----|----|----------|------|
| env_id | BIGINT | O | | O | 환경 정보 ID (AUTO_INCREMENT) |
| item_name | VARCHAR(100) | | | O | 점검 항목명 |
| value | DECIMAL(10,4) | | | | 측정 수치 |
| unit | VARCHAR(20) | | | | 단위 |
| measured_date | DATE | | | | 측정일 |
| latitude | DECIMAL(10,7) | | | O | 위도 |
| longitude | DECIMAL(10,7) | | | O | 경도 |
| created_at | DATETIME | | | O | 데이터 등록일시 |

**인덱스**: PRIMARY KEY (env_id), INDEX (latitude, longitude), INDEX (measured_date)

---

## notice (공지사항)

**목적**: 서비스 공지사항 정보를 저장한다. 관리자 권한 회원만 작성 가능.

| 컬럼명 | 타입 | PK | FK | NOT NULL | 설명 |
|--------|------|----|----|----------|------|
| notice_id | BIGINT | O | | O | 공지사항 ID (AUTO_INCREMENT) |
| member_id | BIGINT | | O | O | 작성자 회원 ID (member 참조) |
| title | VARCHAR(200) | | | O | 제목 |
| content | TEXT | | | O | 내용 |
| created_at | DATETIME | | | O | 작성일시 |
| updated_at | DATETIME | | | | 수정일시 |

**인덱스**: PRIMARY KEY (notice_id), INDEX (member_id), INDEX (created_at)

---

## route_node (경로 노드)

**목적**: A* 알고리즘 경로 탐색에 사용하는 그래프 노드다. 교차로, 주요 지점 등 실제 경로상의 위치를 모델링한다.

| 컬럼명 | 타입 | PK | FK | NOT NULL | 설명 |
|--------|------|----|----|----------|------|
| node_id | BIGINT | O | | O | 노드 ID (AUTO_INCREMENT) |
| latitude | DECIMAL(10,7) | | | O | 위도 |
| longitude | DECIMAL(10,7) | | | O | 경도 |
| node_name | VARCHAR(100) | | | | 노드 명칭 (교차로명 등, 선택) |

**인덱스**: PRIMARY KEY (node_id), INDEX (latitude, longitude)

---

## route_edge (경로 엣지)

**목적**: A* 알고리즘에서 두 노드를 연결하는 간선이다. 이동 거리(가중치)를 저장한다.

| 컬럼명 | 타입 | PK | FK | NOT NULL | 설명 |
|--------|------|----|----|----------|------|
| edge_id | BIGINT | O | | O | 엣지 ID (AUTO_INCREMENT) |
| from_node_id | BIGINT | | O | O | 출발 노드 ID (route_node 참조) |
| to_node_id | BIGINT | | O | O | 도착 노드 ID (route_node 참조) |
| distance | DOUBLE | | | O | 거리 (미터) |

**인덱스**: PRIMARY KEY (edge_id), INDEX (from_node_id), INDEX (to_node_id)

> route_edge는 방향성 그래프(단방향)로 설계한다. 양방향 이동이 가능한 경로는 두 방향 엣지를 모두 삽입한다.
