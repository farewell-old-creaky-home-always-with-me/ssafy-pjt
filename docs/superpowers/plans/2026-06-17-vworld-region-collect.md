# VWorld 법정동 수집 구현 계획

> **For agentic workers:** REQUIRED SUB-SKILL: superpowers:subagent-driven-development or executing-plans

**Goal:** VWorld API로 전국 법정동을 `region_code`에 upsert하는 `regionCodeCollectJob` 구현

**Architecture:** `houseDealCollectJob`과 동일 chunk Job. `external/vworld` 클라이언트 + batch Reader/Processor/Writer + Admin API

**Tech Stack:** Spring Batch 5, MyBatis, RestClient, MockRestServiceServer

---

## 완료 항목

- [x] `github/master` + `feat/req-house-001` merge
- [x] `feat/region-code-collect-job` 브랜치
- [x] VWorld client / batch job / admin API / tests

## 로컬 실행 전 설정

```bash
export VWORLD_API_KEY="발급키"
export VWORLD_DOMAIN="localhost"   # VWorld 등록 도메인
export MOLIT_SERVICE_KEY="..."     # 기존 배치와 동일
export MOLIT_APARTMENT_SALE_URL="..."
export MOLIT_MULTI_FAMILY_SALE_URL="..."
export BATCH_JDBC_INITIALIZE_SCHEMA=always  # 최초 1회
```

Admin: `POST /api/admin/batch/region-codes` (관리자 세션)
