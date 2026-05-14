# API 명세

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-14
- 관련 요구사항: REQ-HOUSE-001, REQ-HOUSE-002, REQ-HOUSE-003, REQ-MEMBER-001, REQ-AUTH-001, REQ-AUTH-002, REQ-FAVORITE-001, REQ-COMMERCIAL-001, REQ-ENV-001, REQ-ROUTE-001, REQ-NOTICE-001
- 관련 문서: [api-overview.md](api-overview.md), [../06_backend/backend-architecture.md](../06_backend/backend-architecture.md)

---

## 회원 API

### POST /api/members — 회원 가입

| 항목 | 내용 |
|------|------|
| 관련 요구사항 | REQ-MEMBER-001 |
| 인증 필요 | 아니오 |

**요청 본문**
```json
{
  "email": "user@example.com",
  "password": "password1234",
  "name": "홍길동"
}
```

**응답 (201)**
```json
{
  "success": true,
  "data": {
    "memberId": 1,
    "email": "user@example.com",
    "name": "홍길동",
    "createdAt": "2026-05-14T10:00:00"
  }
}
```

**오류 케이스**
- 400: 이메일 형식 오류, 비밀번호 길이 미충족
- 409: 이미 사용 중인 이메일 (`DUPLICATE_EMAIL`)

---

### GET /api/members/me — 회원 정보 조회

| 항목 | 내용 |
|------|------|
| 관련 요구사항 | REQ-MEMBER-002 |
| 인증 필요 | 예 |

**응답 (200)**
```json
{
  "success": true,
  "data": {
    "memberId": 1,
    "email": "user@example.com",
    "name": "홍길동",
    "createdAt": "2026-05-14T10:00:00"
  }
}
```

**오류 케이스**
- 401: 로그인하지 않은 상태 (`UNAUTHORIZED`)

---

### PUT /api/members/me — 회원 정보 수정

| 항목 | 내용 |
|------|------|
| 관련 요구사항 | REQ-MEMBER-003 |
| 인증 필요 | 예 |

**요청 본문**
```json
{
  "name": "김철수",
  "password": "newpassword5678"
}
```

**응답 (200)**
```json
{
  "success": true,
  "data": { "memberId": 1, "name": "김철수" }
}
```

**오류 케이스**
- 400: 유효성 검사 실패
- 401: 미인증

---

### DELETE /api/members/me — 회원 탈퇴

| 항목 | 내용 |
|------|------|
| 관련 요구사항 | REQ-MEMBER-004 |
| 인증 필요 | 예 |

**응답 (204)**: 본문 없음

**오류 케이스**
- 401: 미인증

---

## 인증 API

### POST /api/auth/login — 로그인

| 항목 | 내용 |
|------|------|
| 관련 요구사항 | REQ-AUTH-001 |
| 인증 필요 | 아니오 |

**요청 본문**
```json
{
  "email": "user@example.com",
  "password": "password1234"
}
```

**응답 (200)**
```json
{
  "success": true,
  "data": {
    "memberId": 1,
    "name": "홍길동",
    "isAdmin": false
  }
}
```

**오류 케이스**
- 400: 이메일 또는 비밀번호 누락
- 401: 이메일/비밀번호 불일치 (`INVALID_CREDENTIALS`)

---

### POST /api/auth/logout — 로그아웃

| 항목 | 내용 |
|------|------|
| 관련 요구사항 | REQ-AUTH-002 |
| 인증 필요 | 예 |

**응답 (200)**
```json
{ "success": true, "message": "로그아웃되었습니다" }
```

---

### GET /api/auth/me — 인증 상태 확인

| 항목 | 내용 |
|------|------|
| 관련 요구사항 | REQ-AUTH-003 |
| 인증 필요 | 아니오 (상태 반환) |

**응답 (200)**
```json
{
  "success": true,
  "data": {
    "isAuthenticated": true,
    "memberId": 1,
    "name": "홍길동",
    "isAdmin": false
  }
}
```
로그인하지 않은 경우 `isAuthenticated: false`, 나머지 필드 null.

---

## 주택 API

### POST /api/admin/house/collect — 주택 거래 데이터 수집

| 항목 | 내용 |
|------|------|
| 관련 요구사항 | REQ-HOUSE-001 |
| 인증 필요 | 예 (관리자 전용) |
| 설명 | 국토교통부 실거래가 데이터를 수집하고 DB에 저장하는 관리자용 API |

**요청 본문**
```json
{
  "regionCode": "1111000000",
  "yearMonth": "202605",
  "houseType": "아파트",
  "dealType": "매매"
}
```

**응답 (200)**
```json
{
  "success": true,
  "data": {
    "collectedCount": 120,
    "skippedCount": 8,
    "failedCount": 2
  }
}
```

**오류 케이스**
- 400: 유효하지 않은 수집 조건
- 403: 관리자 권한 필요
- 502: 외부 공공 데이터 API 호출 실패

---

### GET /api/houses — 주택 거래 목록 검색

| 항목 | 내용 |
|------|------|
| 관련 요구사항 | REQ-HOUSE-002, REQ-HOUSE-004, REQ-HOUSE-005 |
| 인증 필요 | 아니오 |

**쿼리 파라미터**

| 파라미터 | 필수 | 설명 |
|---------|------|------|
| regionCode | O | 행정구역 코드 |
| houseType | | 주택 유형 (아파트, 다세대) |
| minAmount | | 최소 거래금액 (만 원) |
| maxAmount | | 최대 거래금액 (만 원) |
| page | | 페이지 번호 (기본 1) |
| size | | 페이지 크기 (기본 20) |

**응답 (200)**
```json
{
  "success": true,
  "data": {
    "items": [
      {
        "houseId": 1,
        "aptName": "서울아파트",
        "jibun": "123",
        "buildYear": 2005,
        "houseType": "아파트",
        "latestDeal": {
          "dealAmount": 80000,
          "dealDate": "2026-04-15",
          "area": 84.5,
          "floor": 10
        }
      }
    ],
    "total": 100,
    "page": 1,
    "size": 20
  }
}
```

**오류 케이스**
- 400: regionCode 누락 또는 형식 오류

---

### GET /api/houses/{houseId} — 주택 상세 조회

| 항목 | 내용 |
|------|------|
| 관련 요구사항 | REQ-HOUSE-003 |
| 인증 필요 | 아니오 |

**경로 파라미터**: `houseId` (BIGINT)

**응답 (200)**
```json
{
  "success": true,
  "data": {
    "houseId": 1,
    "aptName": "서울아파트",
    "regionCode": "1100000000",
    "jibun": "123",
    "buildYear": 2005,
    "houseType": "아파트",
    "latitude": 37.5665,
    "longitude": 126.9780,
    "deals": [
      {
        "dealId": 10,
        "dealAmount": 80000,
        "dealDate": "2026-04-15",
        "area": 84.5,
        "floor": 10
      }
    ]
  }
}
```

**오류 케이스**
- 404: houseId 존재하지 않음 (`HOUSE_NOT_FOUND`)

---

## 관심 지역 API

### GET /api/favorites — 관심 지역 목록 조회

| 항목 | 내용 |
|------|------|
| 관련 요구사항 | REQ-FAVORITE-002 |
| 인증 필요 | 예 |

**응답 (200)**
```json
{
  "success": true,
  "data": {
    "items": [
      {
        "favoriteId": 1,
        "regionCode": "1100000000",
        "sidoName": "서울특별시",
        "sigunguName": "종로구",
        "dongName": "청운동",
        "createdAt": "2026-05-01T09:00:00"
      }
    ]
  }
}
```

---

### POST /api/favorites — 관심 지역 등록

| 항목 | 내용 |
|------|------|
| 관련 요구사항 | REQ-FAVORITE-001 |
| 인증 필요 | 예 |

**요청 본문**
```json
{ "regionCode": "1100000000" }
```

**응답 (201)**
```json
{
  "success": true,
  "data": { "favoriteId": 1, "regionCode": "1100000000" }
}
```

**오류 케이스**
- 400: 유효하지 않은 regionCode
- 409: 이미 등록된 관심 지역 (`DUPLICATE_FAVORITE`)

---

### DELETE /api/favorites/{favoriteId} — 관심 지역 삭제

| 항목 | 내용 |
|------|------|
| 관련 요구사항 | REQ-FAVORITE-003 |
| 인증 필요 | 예 |

**응답 (204)**: 본문 없음

**오류 케이스**
- 403: 본인 관심 지역이 아님
- 404: favoriteId 없음

---

## 상권 정보 API

### GET /api/commercial — 주변 상권 조회

| 항목 | 내용 |
|------|------|
| 관련 요구사항 | REQ-COMMERCIAL-001, REQ-COMMERCIAL-002 |
| 인증 필요 | 아니오 |

**쿼리 파라미터**

| 파라미터 | 필수 | 설명 |
|---------|------|------|
| lat | O | 중심 위도 |
| lng | O | 중심 경도 |
| radius | | 반경 (미터, 기본 500) |
| category | | 업종 대분류 필터 |

**응답 (200)**
```json
{
  "success": true,
  "data": {
    "items": [
      {
        "commercialId": 1,
        "bizName": "스타벅스 종로점",
        "categoryLarge": "음식점",
        "categoryMedium": "커피/음료",
        "latitude": 37.5700,
        "longitude": 126.9800,
        "distance": 230
      }
    ]
  }
}
```

---

## 환경 정보 API

### GET /api/environment — 주변 환경 정보 조회

| 항목 | 내용 |
|------|------|
| 관련 요구사항 | REQ-ENV-001 |
| 인증 필요 | 아니오 |

**쿼리 파라미터**

| 파라미터 | 필수 | 설명 |
|---------|------|------|
| lat | O | 중심 위도 |
| lng | O | 중심 경도 |
| radius | | 반경 (미터, 기본 1000) |

**응답 (200)**
```json
{
  "success": true,
  "data": {
    "items": [
      {
        "envId": 1,
        "itemName": "미세먼지",
        "value": 25.0,
        "unit": "μg/m³",
        "measuredDate": "2026-05-13",
        "latitude": 37.5665,
        "longitude": 126.9780
      }
    ]
  }
}
```

**오류 케이스**
- 400: lat 또는 lng 누락
- 404: 서비스 범위 외 지역 (`UNSUPPORTED_REGION`)

---

## 경로 탐색 API

### GET /api/routes — A* 경로 탐색

| 항목 | 내용 |
|------|------|
| 관련 요구사항 | REQ-ROUTE-001 |
| 인증 필요 | 예 |

**쿼리 파라미터**

| 파라미터 | 필수 | 설명 |
|---------|------|------|
| startLat | O | 출발지 위도 |
| startLng | O | 출발지 경도 |
| endLat | O | 목적지 위도 |
| endLng | O | 목적지 경도 |

**응답 (200)**
```json
{
  "success": true,
  "data": {
    "totalDistance": 1250.5,
    "estimatedTime": 15,
    "searchedNodeCount": 340,
    "path": [
      { "nodeId": 10, "latitude": 37.5665, "longitude": 126.9780 },
      { "nodeId": 15, "latitude": 37.5670, "longitude": 126.9790 },
      { "nodeId": 22, "latitude": 37.5680, "longitude": 126.9800 }
    ]
  }
}
```

**응답 필드 설명**

| 필드 | 타입 | 설명 |
|------|------|------|
| totalDistance | Double | 총 이동 거리 (미터) |
| estimatedTime | Int | 예상 소요 시간 (분, 보행 기준) |
| searchedNodeCount | Int | A* 탐색 중 방문한 노드 수 |
| path | Array | 경로를 구성하는 노드 목록 (순서 있음) |

**오류 케이스**
- 400: 필수 파라미터 누락
- 401: 로그인하지 않은 상태 (`UNAUTHORIZED`)
- 404: 출발지 또는 목적지 주변에 그래프 노드 없음 (`NODE_NOT_FOUND`)
- 422: 경로 없음 (그래프 연결 끊김) (`ROUTE_NOT_FOUND`)

---

## 공지사항 API

### GET /api/notices — 공지사항 목록 조회

| 항목 | 내용 |
|------|------|
| 관련 요구사항 | REQ-NOTICE-001 |
| 인증 필요 | 아니오 |

**쿼리 파라미터**: page, size (선택)

**응답 (200)**
```json
{
  "success": true,
  "data": {
    "items": [
      {
        "noticeId": 1,
        "title": "서비스 점검 안내",
        "authorName": "관리자",
        "createdAt": "2026-05-10T10:00:00"
      }
    ],
    "total": 5,
    "page": 1,
    "size": 20
  }
}
```

---

### GET /api/notices/{noticeId} — 공지사항 상세 조회

| 항목 | 내용 |
|------|------|
| 관련 요구사항 | REQ-NOTICE-002 |
| 인증 필요 | 아니오 |

**응답 (200)**
```json
{
  "success": true,
  "data": {
    "noticeId": 1,
    "title": "서비스 점검 안내",
    "content": "5월 15일 오전 2시~4시 서비스 점검이 있습니다.",
    "authorName": "관리자",
    "createdAt": "2026-05-10T10:00:00",
    "updatedAt": null
  }
}
```

**오류 케이스**
- 404: 공지사항 없음 (`NOTICE_NOT_FOUND`)

---

### POST /api/notices — 공지사항 작성

| 항목 | 내용 |
|------|------|
| 관련 요구사항 | REQ-NOTICE-003 |
| 인증 필요 | 예 (관리자) |

**요청 본문**
```json
{ "title": "점검 안내", "content": "내용입니다." }
```

**응답 (201)**
```json
{
  "success": true,
  "data": { "noticeId": 2 }
}
```

**오류 케이스**
- 403: 관리자 권한 없음 (`FORBIDDEN`)

---

### PUT /api/notices/{noticeId} — 공지사항 수정

| 항목 | 내용 |
|------|------|
| 관련 요구사항 | REQ-NOTICE-004 |
| 인증 필요 | 예 (관리자) |

**요청 본문**
```json
{ "title": "수정된 제목", "content": "수정된 내용" }
```

**응답 (200)**
```json
{ "success": true, "data": { "noticeId": 2 } }
```

---

### DELETE /api/notices/{noticeId} — 공지사항 삭제

| 항목 | 내용 |
|------|------|
| 관련 요구사항 | REQ-NOTICE-005 |
| 인증 필요 | 예 (관리자) |

**응답 (204)**: 본문 없음

**오류 케이스**
- 403: 권한 없음
- 404: 공지사항 없음
