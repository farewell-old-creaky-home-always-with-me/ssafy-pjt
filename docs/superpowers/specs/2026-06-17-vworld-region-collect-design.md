# VWorld 법정동 수집 설계

- 상태: 승인됨
- 작성일: 2026-06-17
- 관련 요구사항: NFR-DATA-002, DS-003

## 목표

VWorld 2D Data API로 전국 법정동(읍·면·동) 코드를 `region_code` 테이블에 upsert한다.

## 확정 결정

| 항목 | 결정 |
|------|------|
| 베이스 | `github/master` + `feat/req-house-001` merge |
| 브랜치 | `feat/region-code-collect-job` (worktree 없음) |
| 수집 범위 | 전국 17개 시도 전량 |
| 실패 정책 | 시도 단위 재시도 후 Job FAILED |
| Job 패턴 | `houseDealCollectJob`과 동일 chunk Job |

## Job

- `regionCodeCollectJob` / `regionCodeCollectStep`
- 파라미터: `syncScope=FULL`, `requestedMemberId`, `requestedAt`(identifying)
- Admin API: `POST /api/admin/batch/region-codes`

## VWorld 연동

- 엔드포인트: `https://api.vworld.kr/req/data` (GetFeature)
- 레이어: `LT_C_ADLEGAL_EMD`
- 시도 17개 순회, `bjcd:like:{sido}*` attrFilter + 페이지네이션
- 설정: `vworld.*` (`VWORLD_API_KEY`, `VWORLD_DOMAIN` 등)

## Processor 규칙

- 10자리 `region_code` 필수
- 폐지 동 제외 (`abol_en=Y` 또는 폐지일 존재)
- 시·군·구만 있는 행 제외

## batch_collection_log

- `data_type = REGION_CODE`
- region/year/house/deal 컬럼 NULL
