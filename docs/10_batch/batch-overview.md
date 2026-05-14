# 배치 개요

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-14
- 관련 요구사항: REQ-HOUSE-001
- 관련 문서: [house-deal-collect-job.md](house-deal-collect-job.md), [batch-operation.md](batch-operation.md), [../06_backend/backend-architecture.md](../06_backend/backend-architecture.md), [../04_database/table-spec.md](../04_database/table-spec.md)

---

## Spring Batch 도입 목적

SSAFY HOME은 공공 데이터를 주기적으로 수집해 서비스 DB에 적재해야 한다. 이 작업은 일반 요청-응답 Service 로직보다 실행 이력, 재시작, 실패 처리, 대량 데이터 적재 관리가 중요하므로 Spring Batch를 시작부터 도입한다.

---

## 배치 처리 대상

- 주택 거래 데이터 수집 (`houseDealCollectJob`)
- 향후 행정구역 코드 수집
- 향후 상권 데이터 수집
- 향후 환경 데이터 수집

현재 필수 구현 대상 Job은 `houseDealCollectJob`이다.

---

## Job, Step, Reader, Processor, Writer 구조

```text
Job
 └─ Step
     ├─ Reader
     ├─ Processor
     ├─ Writer
     └─ Listener
```

| 구성 요소 | 역할 |
|-----------|------|
| Job | 배치 실행 단위. 파라미터와 실행 상태를 관리한다 |
| Step | 읽기·가공·저장 흐름을 나누는 실행 단위 |
| Reader | 외부 공공 데이터 API 또는 내부 저장소에서 데이터를 읽는다 |
| Processor | 검증, 정규화, 중복 판정, 형 변환을 수행한다 |
| Writer | `house`, `house_deal` 같은 서비스 테이블에 반영한다 |
| Listener | 실행 시작/종료 로그와 수집 요약 로그를 남긴다 |

---

## 재시도, 스킵, 실패 이력 관리 원칙

- 일시적 외부 API 오류는 재시도 정책으로 대응한다.
- 레코드 단위 데이터 오류는 스킵 정책으로 분리할 수 있다.
- Job 전체 실패 여부와 Step 처리 건수는 Spring Batch 실행 메타데이터로 추적한다.
- 서비스 관점 수집 요약은 `batch_collection_log`에 남긴다.
- 재시작은 실패 상태 Job만 허용하고, 파라미터와 이전 실행 상태를 기준으로 판단한다.

---

## Spring Batch 메타데이터 테이블과 `batch_collection_log`의 역할 차이

| 구분 | 역할 |
|------|------|
| Spring Batch 메타데이터 테이블 | Job/Step 실행 상태, 실행 시각, 재시작 가능 여부 같은 프레임워크 수준 이력을 관리한다 |
| `batch_collection_log` | 어떤 조건으로 무엇을 수집했고 몇 건이 반영·스킵·실패했는지 서비스 관점 요약을 관리한다 |

Spring Batch 메타데이터 테이블은 MySQL용 기본 `schema-mysql.sql`로 생성한다. `schema.sql`에는 서비스 전용 로그 테이블인 `batch_collection_log`만 직접 정의한다.
