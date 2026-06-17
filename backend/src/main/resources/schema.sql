-- SSAFY HOME 초기 DDL 스크립트
-- 작성일: 2026-05-15
-- 상태: 완료

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- 행정구역 코드 (VWorld 기준)
-- ============================================================
CREATE TABLE IF NOT EXISTS region_code (
    region_code  VARCHAR(10)  NOT NULL COMMENT '행정구역 코드 (10자리)',
    sido_name    VARCHAR(20)  NOT NULL COMMENT '시도명',
    sigungu_name VARCHAR(30)           COMMENT '시군구명 (시도 단위이면 NULL)',
    dong_name    VARCHAR(30)           COMMENT '읍면동명 (시군구 단위이면 NULL)',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '데이터 등록일시',

    PRIMARY KEY (region_code),
    INDEX idx_region_sido_sigungu (sido_name, sigungu_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='행정구역 코드';

-- ============================================================
-- 주택 단지 (아파트·다세대)
-- ============================================================
CREATE TABLE IF NOT EXISTS house (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '주택 ID',
    region_code  VARCHAR(10)  NOT NULL COMMENT '행정구역 코드',
    apt_name     VARCHAR(100) NOT NULL COMMENT '단지명',
    jibun        VARCHAR(50)  NOT NULL COMMENT '지번 주소',
    road_address VARCHAR(100)          COMMENT '도로명 주소 (미정)',
    build_year   INT                   COMMENT '건축연도',
    house_type   VARCHAR(10)  NOT NULL COMMENT '주택 유형: 아파트, 다세대',
    latitude     DECIMAL(10,7)         COMMENT '위도',
    longitude    DECIMAL(10,7)         COMMENT '경도',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '데이터 등록일시',

    PRIMARY KEY (id),
    FOREIGN KEY (region_code) REFERENCES region_code(region_code) ON UPDATE CASCADE,
    INDEX idx_house_region (region_code),
    INDEX idx_house_name (apt_name),
    UNIQUE KEY uq_house_identity (region_code, apt_name, jibun, house_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='주택 단지';

-- ============================================================
-- 주택 거래 이력 (국토교통부 실거래가 데이터)
-- ============================================================
CREATE TABLE IF NOT EXISTS house_deal (
    id             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '거래 ID',
    house_id       BIGINT       NOT NULL COMMENT '주택 ID',
    deal_type      VARCHAR(20)  NOT NULL COMMENT '거래 유형: 매매, 전세, 월세',
    deal_amount    INT                   COMMENT '매매 거래금액 (만원)',
    deposit_amount INT                   COMMENT '보증금액 (만원)',
    monthly_rent   INT          NOT NULL DEFAULT 0 COMMENT '월세금액 (만원, 전세는 0)',
    deal_date      DATE         NOT NULL COMMENT '거래일',
    area           DECIMAL(6,2) NOT NULL COMMENT '전용면적 (㎡)',
    floor          INT                   COMMENT '층수',
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '데이터 등록일시',

    PRIMARY KEY (id),
    FOREIGN KEY (house_id) REFERENCES house(id) ON DELETE CASCADE,
    INDEX idx_deal_house (house_id),
    INDEX idx_deal_type (deal_type),
    INDEX idx_deal_date (deal_date),
    INDEX idx_deal_amount (deal_amount)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='주택 거래 이력';

-- ============================================================
-- Spring Batch 메타데이터 테이블
-- ============================================================
-- Spring Batch 기본 메타데이터 테이블은 framework 기본 schema-mysql.sql을 사용해 생성한다.
-- 서비스 전용 로그만 아래 batch_collection_log 테이블로 별도 관리한다.

CREATE TABLE IF NOT EXISTS batch_collection_log (
    id                BIGINT       NOT NULL AUTO_INCREMENT COMMENT '수집 로그 ID',
    job_execution_id  BIGINT       NOT NULL COMMENT 'Spring Batch Job 실행 ID',
    job_name          VARCHAR(100) NOT NULL COMMENT 'Job 이름',
    data_type         VARCHAR(30)  NOT NULL COMMENT '수집 데이터 유형',
    region_code       VARCHAR(10)           COMMENT '행정구역 코드',
    `year_month`      VARCHAR(6)            COMMENT '수집 기준 연월',
    house_type        VARCHAR(20)           COMMENT '주택 유형',
    deal_type         VARCHAR(20)           COMMENT '거래 유형',
    collected_count   INT          NOT NULL DEFAULT 0 COMMENT '수집 반영 건수',
    skipped_count     INT          NOT NULL DEFAULT 0 COMMENT '중복 등 스킵 건수',
    failed_count      INT          NOT NULL DEFAULT 0 COMMENT '실패 건수',
    status            VARCHAR(20)  NOT NULL COMMENT '실행 상태',
    started_at        DATETIME              COMMENT 'Job 시작 시각',
    ended_at          DATETIME              COMMENT 'Job 종료 시각',
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '로그 생성 시각',

    PRIMARY KEY (id),
    FOREIGN KEY (region_code) REFERENCES region_code(region_code) ON UPDATE CASCADE,
    INDEX idx_collection_job_execution (job_execution_id),
    INDEX idx_collection_condition (region_code, `year_month`, house_type, deal_type),
    INDEX idx_collection_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='배치 수집 로그';

-- ============================================================
-- 회원
-- ============================================================
CREATE TABLE IF NOT EXISTS member (
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '회원 ID',
    email      VARCHAR(100) NOT NULL COMMENT '이메일 (로그인 ID)',
    password   VARCHAR(255) NOT NULL COMMENT '비밀번호 (bcrypt 해시)',
    name       VARCHAR(50)  NOT NULL COMMENT '이름',
    is_admin   TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '관리자 여부 (0: 일반, 1: 관리자)',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '가입일시',
    updated_at DATETIME              COMMENT '정보 수정일시',

    PRIMARY KEY (id),
    UNIQUE KEY uq_member_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='회원';

-- ============================================================
-- 관심 지역
-- ============================================================
CREATE TABLE IF NOT EXISTS favorite_area (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '관심 지역 ID',
    member_id   BIGINT      NOT NULL COMMENT '회원 ID',
    region_code VARCHAR(10) NOT NULL COMMENT '행정구역 코드',
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',

    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    FOREIGN KEY (region_code) REFERENCES region_code(region_code) ON UPDATE CASCADE,
    INDEX idx_favorite_member (member_id),
    UNIQUE KEY uq_favorite_member_region (member_id, region_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='관심 지역';

-- ============================================================
-- 회원 장소
-- ============================================================
CREATE TABLE IF NOT EXISTS member_place (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '장소 ID',
    member_id   BIGINT        NOT NULL COMMENT '회원 ID',
    place_type  VARCHAR(10)   NOT NULL COMMENT '장소 유형: HOME, WORK, OTHER',
    name        VARCHAR(50)   NOT NULL COMMENT '장소 표시 이름',
    address     VARCHAR(150)  NOT NULL COMMENT '주소',
    region_code VARCHAR(10)            COMMENT '행정구역 코드',
    latitude    DECIMAL(10,7) NOT NULL COMMENT '위도',
    longitude   DECIMAL(10,7) NOT NULL COMMENT '경도',
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    updated_at  DATETIME               COMMENT '수정일시',

    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    FOREIGN KEY (region_code) REFERENCES region_code(region_code) ON UPDATE CASCADE,
    INDEX idx_member_place_member (member_id),
    INDEX idx_member_place_member_type (member_id, place_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='회원 장소';

-- ============================================================
-- 공지사항
-- ============================================================
CREATE TABLE IF NOT EXISTS notice (
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '공지사항 ID',
    member_id  BIGINT       NOT NULL COMMENT '작성자 회원 ID',
    title      VARCHAR(200) NOT NULL COMMENT '제목',
    content    TEXT         NOT NULL COMMENT '내용',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '작성일시',
    updated_at DATETIME              COMMENT '수정일시',

    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member(id),
    INDEX idx_notice_member (member_id),
    INDEX idx_notice_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='공지사항';

-- ============================================================
-- 상권 정보
-- ============================================================
CREATE TABLE IF NOT EXISTS commercial_area (
    id              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '상권 ID',
    biz_name        VARCHAR(100)  NOT NULL COMMENT '상호명',
    category_large  VARCHAR(50)            COMMENT '업종 대분류',
    category_medium VARCHAR(50)            COMMENT '업종 중분류',
    category_small  VARCHAR(50)            COMMENT '업종 소분류',
    latitude        DECIMAL(10,7) NOT NULL COMMENT '위도',
    longitude       DECIMAL(10,7) NOT NULL COMMENT '경도',
    address         VARCHAR(150)           COMMENT '주소',
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '데이터 등록일시',

    PRIMARY KEY (id),
    INDEX idx_commercial_location (latitude, longitude)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='상권 정보';

-- ============================================================
-- 환경 정보
-- ============================================================
CREATE TABLE IF NOT EXISTS environment_info (
    id            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '환경 정보 ID',
    item_name     VARCHAR(100)  NOT NULL COMMENT '점검 항목명',
    value         DECIMAL(10,4)          COMMENT '측정 수치',
    unit          VARCHAR(20)            COMMENT '단위',
    measured_date DATE                   COMMENT '측정일',
    latitude      DECIMAL(10,7) NOT NULL COMMENT '위도',
    longitude     DECIMAL(10,7) NOT NULL COMMENT '경도',
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '데이터 등록일시',

    PRIMARY KEY (id),
    INDEX idx_env_location (latitude, longitude),
    INDEX idx_env_date (measured_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='환경 정보';

-- ============================================================
-- 시설물 (A* 그래프 노드 — 지하철역, 학교, 병원, 공원 등)
-- ============================================================
CREATE TABLE IF NOT EXISTS facility (
    facility_id   BIGINT        NOT NULL AUTO_INCREMENT COMMENT '시설물 ID',
    name          VARCHAR(100)  NOT NULL COMMENT '시설물명',
    facility_type VARCHAR(30)   NOT NULL COMMENT '유형: SUBWAY, SCHOOL, HOSPITAL, PARK 등',
    address       VARCHAR(200)           COMMENT '주소',
    latitude      DECIMAL(10,7) NOT NULL COMMENT '위도',
    longitude     DECIMAL(10,7) NOT NULL COMMENT '경도',
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',

    PRIMARY KEY (facility_id),
    INDEX idx_facility_location (latitude, longitude),
    INDEX idx_facility_type (facility_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='시설물 (A* 경로 탐색 노드)';

-- ============================================================
-- A* 경로 탐색 요청 결과
-- ============================================================
CREATE TABLE IF NOT EXISTS route_request (
    route_request_id BIGINT   NOT NULL AUTO_INCREMENT COMMENT '경로 탐색 요청 ID',
    member_id        BIGINT   NOT NULL COMMENT '회원 ID',
    house_id         BIGINT   NOT NULL COMMENT '출발 매물 ID',
    place_id         BIGINT   NOT NULL COMMENT '도착 장소 ID',
    total_dist_m     INT      NOT NULL COMMENT '총 거리 (미터)',
    node_count       INT      NOT NULL COMMENT '경로 좌표 수',
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '요청일시',

    PRIMARY KEY (route_request_id),
    FOREIGN KEY (member_id) REFERENCES member(id),
    FOREIGN KEY (house_id)  REFERENCES house(id),
    FOREIGN KEY (place_id)  REFERENCES member_place(id),
    INDEX idx_route_request_member (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='A* 경로 탐색 요청 결과';

-- ============================================================
-- A* 경로 좌표 목록
-- ============================================================
CREATE TABLE IF NOT EXISTS route_path (
    route_path_id    BIGINT        NOT NULL AUTO_INCREMENT COMMENT '경로 좌표 ID',
    route_request_id BIGINT        NOT NULL COMMENT '경로 탐색 요청 ID',
    seq              INT           NOT NULL COMMENT '순서 (0부터)',
    latitude         DECIMAL(10,7) NOT NULL COMMENT '위도',
    longitude        DECIMAL(10,7) NOT NULL COMMENT '경도',

    PRIMARY KEY (route_path_id),
    FOREIGN KEY (route_request_id) REFERENCES route_request(route_request_id) ON DELETE CASCADE,
    INDEX idx_route_path_request (route_request_id),
    UNIQUE KEY uq_route_path_order (route_request_id, seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='A* 경로 좌표 목록';

SET FOREIGN_KEY_CHECKS = 1;
