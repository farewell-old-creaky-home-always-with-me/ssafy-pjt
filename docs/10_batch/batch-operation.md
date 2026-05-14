# 배치 운영

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-14
- 관련 요구사항: REQ-HOUSE-001
- 관련 문서: [batch-overview.md](batch-overview.md), [house-deal-collect-job.md](house-deal-collect-job.md), [../05_api/api-spec.md](../05_api/api-spec.md)

---

## 관리자 API로 Job을 실행하는 방식

- 관리자는 `POST /api/admin/batch/house-deals`로 `houseDealCollectJob` 실행을 요청한다.
- Controller는 요청 파라미터를 검증한 뒤 Job 실행만 위임한다.
- 실제 수집, 검증, 적재는 Spring Batch 내부에서 수행한다.

---

## Job 실행 상태 확인 방법

- `GET /api/admin/batch/jobs`로 최근 실행 목록을 조회한다.
- `GET /api/admin/batch/jobs/{jobExecutionId}`로 특정 실행의 상세 상태와 Step 처리 건수를 확인한다.
- 서비스 관점 수집 요약은 `batch_collection_log`를 함께 조회해 확인한다.

---

## 실패 Job 재시작 방법

- `POST /api/admin/batch/jobs/{jobExecutionId}/restart`를 사용한다.
- 실패 상태이며 재시작 가능한 실행만 허용한다.
- 재시작 후 새 `jobExecutionId`가 생성된다.

---

## 운영 시 주의사항

- 같은 조건의 Job을 동시에 여러 번 실행하지 않는다.
- 외부 공공 데이터 API 호출 제한과 응답 지연을 고려한다.
- 관리자 권한 없는 사용자는 배치 API에 접근할 수 없어야 한다.
- 대량 수집 시 실행 시간과 DB 부하를 모니터링한다.

---

## 로그 및 모니터링 포인트

- Job 시작/종료 시각
- Job 상태 (`STARTED`, `COMPLETED`, `FAILED`)
- Step별 read/write/skip 건수
- `batch_collection_log`의 collected/skipped/failed 집계
- 외부 API 오류 발생 횟수와 재시도 횟수
