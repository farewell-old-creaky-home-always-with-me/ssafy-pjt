-- =========================================================
-- WhereIsMyHouse AI Navigator
-- DB Schema (DDL SQL)
-- MySQL 8.x 기준
--
-- 목적:
-- 1) SSAFY HOME DB PJT 필수 요구사항 충족
--    - 실거래가 정보 저장/조회
--    - 회원정보 관리
--    - 관심지역 관리
-- 2) 추가/심화 요구사항 확장
--    - 상권/환경/CCTV/동네 구성원 정보
--    - 공지사항/게시판
--    - A* 경로 탐색 결과 저장
--    - ChatBot/RAG 문서 업로드 및 검색 이력 저장
-- =========================================================

DROP DATABASE IF EXISTS whereismyhouse;
CREATE DATABASE whereismyhouse
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE whereismyhouse;

-- =========================================================
-- 1. 회원 / 인증
-- =========================================================

CREATE TABLE members (
    member_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone_encrypted VARCHAR(255),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_members_login_id UNIQUE (login_id),
    CONSTRAINT uk_members_email UNIQUE (email),
    CONSTRAINT chk_members_role CHECK (role IN ('USER', 'ADMIN')),
    CONSTRAINT chk_members_status CHECK (status IN ('ACTIVE', 'WITHDRAWN', 'LOCKED'))
) ENGINE=InnoDB;

CREATE TABLE password_reset_tokens (
    token_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL,
    expired_at DATETIME NOT NULL,
    used_yn CHAR(1) NOT NULL DEFAULT 'N',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_password_reset_tokens_member
        FOREIGN KEY (member_id) REFERENCES members(member_id)
        ON DELETE CASCADE,
    CONSTRAINT uk_password_reset_tokens_token UNIQUE (token),
    CONSTRAINT chk_password_reset_tokens_used CHECK (used_yn IN ('Y', 'N'))
) ENGINE=InnoDB;


-- =========================================================
-- 2. 지역 / 법정동
-- =========================================================

CREATE TABLE regions (
    region_code VARCHAR(20) PRIMARY KEY,
    sido VARCHAR(50) NOT NULL,
    sigungu VARCHAR(50) NOT NULL,
    dong VARCHAR(50) NOT NULL,
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),

    INDEX idx_regions_sido_sigungu_dong (sido, sigungu, dong),
    INDEX idx_regions_dong (dong)
) ENGINE=InnoDB;


-- =========================================================
-- 3. 주택 기본 정보 / 실거래가 정보
-- =========================================================

CREATE TABLE properties (
    property_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    region_code VARCHAR(20) NOT NULL,
    property_type VARCHAR(20) NOT NULL,
    property_name VARCHAR(100) NOT NULL,
    jibun VARCHAR(50),
    road_address VARCHAR(255),
    build_year INT,
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_properties_region
        FOREIGN KEY (region_code) REFERENCES regions(region_code),

    CONSTRAINT chk_properties_type
        CHECK (property_type IN ('APT', 'VILLA')),

    INDEX idx_properties_region (region_code),
    INDEX idx_properties_name (property_name),
    INDEX idx_properties_type (property_type),
    INDEX idx_properties_location (latitude, longitude)
) ENGINE=InnoDB;

CREATE TABLE property_images (
    image_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    property_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    image_type VARCHAR(30) DEFAULT 'MAIN',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_property_images_property
        FOREIGN KEY (property_id) REFERENCES properties(property_id)
        ON DELETE CASCADE,

    INDEX idx_property_images_property (property_id)
) ENGINE=InnoDB;

CREATE TABLE deals (
    deal_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    property_id BIGINT NOT NULL,
    deal_type VARCHAR(20) NOT NULL,
    deal_amount BIGINT,
    deposit_amount BIGINT,
    monthly_rent BIGINT,
    deal_year INT NOT NULL,
    deal_month INT NOT NULL,
    deal_day INT NOT NULL,
    floor INT,
    exclusive_area DECIMAL(10, 2),
    source_file VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_deals_property
        FOREIGN KEY (property_id) REFERENCES properties(property_id)
        ON DELETE CASCADE,

    CONSTRAINT chk_deals_type
        CHECK (deal_type IN ('SALE', 'RENT')),

    CONSTRAINT chk_deals_month
        CHECK (deal_month BETWEEN 1 AND 12),

    CONSTRAINT chk_deals_day
        CHECK (deal_day BETWEEN 1 AND 31),

    INDEX idx_deals_property (property_id),
    INDEX idx_deals_type (deal_type),
    INDEX idx_deals_date (deal_year, deal_month, deal_day),
    INDEX idx_deals_amount (deal_amount),
    INDEX idx_deals_deposit (deposit_amount),
    INDEX idx_deals_monthly_rent (monthly_rent)
) ENGINE=InnoDB;


-- =========================================================
-- 4. 관심지역 / 관심매물
-- =========================================================

CREATE TABLE favorite_regions (
    favorite_region_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    region_code VARCHAR(20) NOT NULL,
    memo VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_favorite_regions_member
        FOREIGN KEY (member_id) REFERENCES members(member_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_favorite_regions_region
        FOREIGN KEY (region_code) REFERENCES regions(region_code)
        ON DELETE CASCADE,

    CONSTRAINT uk_favorite_regions_member_region
        UNIQUE (member_id, region_code),

    INDEX idx_favorite_regions_member (member_id),
    INDEX idx_favorite_regions_region (region_code)
) ENGINE=InnoDB;

CREATE TABLE favorite_properties (
    favorite_property_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    property_id BIGINT NOT NULL,
    memo VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_favorite_properties_member
        FOREIGN KEY (member_id) REFERENCES members(member_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_favorite_properties_property
        FOREIGN KEY (property_id) REFERENCES properties(property_id)
        ON DELETE CASCADE,

    CONSTRAINT uk_favorite_properties_member_property
        UNIQUE (member_id, property_id),

    INDEX idx_favorite_properties_member (member_id),
    INDEX idx_favorite_properties_property (property_id)
) ENGINE=InnoDB;


-- =========================================================
-- 5. 주변 상권 / 환경 / 동네 구성원 / CCTV
-- =========================================================

CREATE TABLE commercial_areas (
    commercial_area_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    region_code VARCHAR(20) NOT NULL,
    business_name VARCHAR(100) NOT NULL,
    business_category VARCHAR(100),
    address VARCHAR(255),
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_commercial_areas_region
        FOREIGN KEY (region_code) REFERENCES regions(region_code)
        ON DELETE CASCADE,

    INDEX idx_commercial_areas_region (region_code),
    INDEX idx_commercial_areas_category (business_category),
    INDEX idx_commercial_areas_location (latitude, longitude)
) ENGINE=InnoDB;

CREATE TABLE environment_infos (
    environment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    region_code VARCHAR(20) NOT NULL,
    environment_type VARCHAR(30) NOT NULL,
    title VARCHAR(150) NOT NULL,
    status VARCHAR(100),
    address VARCHAR(255),
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    measured_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_environment_infos_region
        FOREIGN KEY (region_code) REFERENCES regions(region_code)
        ON DELETE CASCADE,

    CONSTRAINT chk_environment_infos_type
        CHECK (environment_type IN ('GREEN', 'WASTEWATER', 'AIR', 'ETC')),

    INDEX idx_environment_infos_region (region_code),
    INDEX idx_environment_infos_type (environment_type),
    INDEX idx_environment_infos_location (latitude, longitude)
) ENGINE=InnoDB;

CREATE TABLE cctv_infos (
    cctv_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    region_code VARCHAR(20) NOT NULL,
    location_name VARCHAR(150) NOT NULL,
    purpose VARCHAR(100),
    camera_count INT NOT NULL DEFAULT 1,
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    installed_at DATE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_cctv_infos_region
        FOREIGN KEY (region_code) REFERENCES regions(region_code)
        ON DELETE CASCADE,

    INDEX idx_cctv_infos_region (region_code),
    INDEX idx_cctv_infos_location (latitude, longitude)
) ENGINE=InnoDB;

CREATE TABLE population_stats (
    population_stat_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    region_code VARCHAR(20) NOT NULL,
    total_population INT,
    foreigner_count INT,
    elderly_count INT,
    stat_year INT NOT NULL,
    stat_month INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_population_stats_region
        FOREIGN KEY (region_code) REFERENCES regions(region_code)
        ON DELETE CASCADE,

    CONSTRAINT chk_population_stats_month
        CHECK (stat_month BETWEEN 1 AND 12),

    CONSTRAINT uk_population_stats_region_period
        UNIQUE (region_code, stat_year, stat_month),

    INDEX idx_population_stats_region (region_code),
    INDEX idx_population_stats_period (stat_year, stat_month)
) ENGINE=InnoDB;


-- =========================================================
-- 6. 커뮤니티 / 공지사항 / 주택 관련 정보
-- =========================================================

CREATE TABLE notices (
    notice_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    view_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_notices_member
        FOREIGN KEY (member_id) REFERENCES members(member_id),

    INDEX idx_notices_member (member_id),
    INDEX idx_notices_created_at (created_at)
) ENGINE=InnoDB;

CREATE TABLE posts (
    post_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(50) NOT NULL DEFAULT 'FREE',
    view_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_posts_member
        FOREIGN KEY (member_id) REFERENCES members(member_id)
        ON DELETE CASCADE,

    INDEX idx_posts_member (member_id),
    INDEX idx_posts_category (category),
    INDEX idx_posts_created_at (created_at)
) ENGINE=InnoDB;

CREATE TABLE housing_news (
    news_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(300) NOT NULL,
    url VARCHAR(500) NOT NULL,
    publisher VARCHAR(100),
    summary TEXT,
    published_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_housing_news_url UNIQUE (url),

    INDEX idx_housing_news_published_at (published_at),
    INDEX idx_housing_news_title (title)
) ENGINE=InnoDB;

CREATE TABLE housing_guides (
    guide_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    guide_type VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_housing_guides_type (guide_type)
) ENGINE=InnoDB;


-- =========================================================
-- 7. 알고리즘 기능: 시설 / A* 경로 탐색 결과
-- =========================================================

CREATE TABLE facilities (
    facility_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    region_code VARCHAR(20) NOT NULL,
    facility_type VARCHAR(50) NOT NULL,
    facility_name VARCHAR(150) NOT NULL,
    address VARCHAR(255),
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_facilities_region
        FOREIGN KEY (region_code) REFERENCES regions(region_code)
        ON DELETE CASCADE,

    INDEX idx_facilities_region (region_code),
    INDEX idx_facilities_type (facility_type),
    INDEX idx_facilities_location (latitude, longitude)
) ENGINE=InnoDB;

CREATE TABLE route_requests (
    route_request_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT,
    property_id BIGINT NOT NULL,
    facility_id BIGINT NOT NULL,
    algorithm_type VARCHAR(30) NOT NULL DEFAULT 'A_STAR',
    total_distance_meter INT,
    estimated_time_minute INT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_route_requests_member
        FOREIGN KEY (member_id) REFERENCES members(member_id)
        ON DELETE SET NULL,

    CONSTRAINT fk_route_requests_property
        FOREIGN KEY (property_id) REFERENCES properties(property_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_route_requests_facility
        FOREIGN KEY (facility_id) REFERENCES facilities(facility_id)
        ON DELETE CASCADE,

    INDEX idx_route_requests_member (member_id),
    INDEX idx_route_requests_property (property_id),
    INDEX idx_route_requests_facility (facility_id),
    INDEX idx_route_requests_created_at (created_at)
) ENGINE=InnoDB;

CREATE TABLE route_paths (
    route_path_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_request_id BIGINT NOT NULL,
    sequence_no INT NOT NULL,
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL,
    distance_from_prev INT DEFAULT 0,

    CONSTRAINT fk_route_paths_route_request
        FOREIGN KEY (route_request_id) REFERENCES route_requests(route_request_id)
        ON DELETE CASCADE,

    CONSTRAINT uk_route_paths_request_sequence
        UNIQUE (route_request_id, sequence_no),

    INDEX idx_route_paths_request (route_request_id)
) ENGINE=InnoDB;


-- =========================================================
-- 8. ChatBot / RAG
-- =========================================================

CREATE TABLE ai_documents (
    document_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL,
    file_type VARCHAR(20) NOT NULL,
    storage_path VARCHAR(500) NOT NULL,
    document_type VARCHAR(50) DEFAULT 'GENERAL',
    uploaded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ai_documents_member
        FOREIGN KEY (member_id) REFERENCES members(member_id)
        ON DELETE SET NULL,

    CONSTRAINT chk_ai_documents_file_type
        CHECK (file_type IN ('TXT', 'MD', 'PDF')),

    INDEX idx_ai_documents_member (member_id),
    INDEX idx_ai_documents_uploaded_at (uploaded_at)
) ENGINE=InnoDB;

CREATE TABLE ai_chunks (
    chunk_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL,
    chunk_index INT NOT NULL,
    content TEXT NOT NULL,
    embedding_id VARCHAR(255),
    token_count INT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ai_chunks_document
        FOREIGN KEY (document_id) REFERENCES ai_documents(document_id)
        ON DELETE CASCADE,

    CONSTRAINT uk_ai_chunks_document_index
        UNIQUE (document_id, chunk_index),

    INDEX idx_ai_chunks_document (document_id),
    INDEX idx_ai_chunks_embedding (embedding_id)
) ENGINE=InnoDB;

CREATE TABLE chat_sessions (
    chat_session_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT,
    title VARCHAR(200),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_chat_sessions_member
        FOREIGN KEY (member_id) REFERENCES members(member_id)
        ON DELETE SET NULL,

    INDEX idx_chat_sessions_member (member_id),
    INDEX idx_chat_sessions_created_at (created_at)
) ENGINE=InnoDB;

CREATE TABLE chat_messages (
    message_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_session_id BIGINT NOT NULL,
    sender_type VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    used_rag BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_chat_messages_session
        FOREIGN KEY (chat_session_id) REFERENCES chat_sessions(chat_session_id)
        ON DELETE CASCADE,

    CONSTRAINT chk_chat_messages_sender
        CHECK (sender_type IN ('USER', 'AI')),

    INDEX idx_chat_messages_session (chat_session_id),
    INDEX idx_chat_messages_created_at (created_at)
) ENGINE=InnoDB;

CREATE TABLE ai_references (
    reference_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id BIGINT NOT NULL,
    chunk_id BIGINT NOT NULL,
    similarity_score DECIMAL(6, 5),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ai_references_message
        FOREIGN KEY (message_id) REFERENCES chat_messages(message_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_ai_references_chunk
        FOREIGN KEY (chunk_id) REFERENCES ai_chunks(chunk_id)
        ON DELETE CASCADE,

    INDEX idx_ai_references_message (message_id),
    INDEX idx_ai_references_chunk (chunk_id),
    INDEX idx_ai_references_similarity (similarity_score)
) ENGINE=InnoDB;


-- =========================================================
-- 9. 추천 / 정렬 결과 저장 옵션
--    Tim Sort 자체는 Java List.sort()/Comparator 영역이지만,
--    사용자의 정렬 조건과 결과를 기록하면 보고서/성능 비교에 활용 가능
-- =========================================================

CREATE TABLE property_sort_logs (
    sort_log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT,
    region_code VARCHAR(20),
    sort_condition VARCHAR(100) NOT NULL,
    result_count INT NOT NULL DEFAULT 0,
    elapsed_ms BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_property_sort_logs_member
        FOREIGN KEY (member_id) REFERENCES members(member_id)
        ON DELETE SET NULL,

    CONSTRAINT fk_property_sort_logs_region
        FOREIGN KEY (region_code) REFERENCES regions(region_code)
        ON DELETE SET NULL,

    INDEX idx_property_sort_logs_member (member_id),
    INDEX idx_property_sort_logs_region (region_code),
    INDEX idx_property_sort_logs_created_at (created_at)
) ENGINE=InnoDB;


-- =========================================================
-- 10. 기본 관리자 계정 예시
-- 실제 프로젝트에서는 비밀번호 해시를 애플리케이션에서 생성해서 넣는 것을 권장
-- =========================================================

-- INSERT INTO members (login_id, password_hash, name, email, role)
-- VALUES ('admin', '{bcrypt_hash_here}', '관리자', 'admin@example.com', 'ADMIN');
