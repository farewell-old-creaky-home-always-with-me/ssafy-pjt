# 주택 거래 수집 Job

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-14
- 관련 요구사항: REQ-HOUSE-001
- 관련 문서: [batch-overview.md](batch-overview.md), [batch-operation.md](batch-operation.md), [../05_api/api-spec.md](../05_api/api-spec.md), [../04_database/table-spec.md](../04_database/table-spec.md)

---

## `houseDealCollectJob` 목적

국토교통부 실거래가 데이터를 읽어 `house`, `house_deal` 테이블에 반영하고, 실행 결과를 `batch_collection_log`에 기록한다.

---

## Job 파라미터

| 파라미터 | 설명 |
|----------|------|
| `regionCode` | 수집 대상 행정구역 코드 |
| `yearMonth` | 수집 대상 연월 (`YYYYMM`) |
| `houseType` | 주택 유형 |
| `dealType` | 거래 유형 |
| `requestedBy` | 실행 요청 관리자 식별자 |

---

## Step 구조

1. 공공 데이터 읽기
2. 데이터 검증 및 정규화
3. `house`, `house_deal` Upsert
4. 수집 로그 기록

---

## Reader, Processor, Writer 책임

| 구성 요소 | 책임 |
|-----------|------|
| Reader | 국토교통부 API 호출, 페이지 순회, 원본 응답 읽기 |
| Processor | 금액·주소·거래일 정규화, `house_type`/`deal_type` 매핑, 중복 후보 판정 |
| Writer | `house` 식별 키 기준 Upsert, `house_deal` 저장, 처리 건수 집계 |

---

## 중복 데이터 처리

- `house`는 `region_code + apt_name + jibun + house_type` 조합으로 식별한다.
- 이미 존재하는 `house`는 재사용하고 신규만 생성한다.
- `house_deal`은 수집 조건과 거래 일자, 면적, 층, 금액 조합을 기준으로 애플리케이션 레벨에서 중복 여부를 판정한다.
- 중복으로 판단된 거래 건은 `skipped_count`에 반영한다.

---

## 실패, 재시도, 스킵 정책

- 네트워크 일시 오류는 제한된 횟수 내 재시도한다.
- 개별 레코드 형식 오류는 스킵 가능 대상으로 분류한다.
- DB 쓰기 실패처럼 전체 정합성에 영향이 큰 오류는 Step 실패로 처리한다.
- 실패와 스킵 건수는 `batch_collection_log`와 Job 실행 상세에서 함께 확인한다.

---

## 재시작 정책

- 실패 상태 Job만 재시작 대상이다.
- 재시작 가능 여부는 Spring Batch 실행 메타데이터를 기준으로 판단한다.
- 동일 파라미터 재실행과 실패 Job 재시작은 운영 정책상 구분한다.
- 재시작 시 새 `jobExecutionId`가 생성되고 이전 실행 이력과 연결해 추적한다.
