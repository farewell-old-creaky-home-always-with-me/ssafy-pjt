SET REFERENTIAL_INTEGRITY FALSE;

DROP TABLE IF EXISTS commercial_area;
DROP TABLE IF EXISTS environment_info;
DROP TABLE IF EXISTS qna;
DROP TABLE IF EXISTS batch_report;
DROP TABLE IF EXISTS batch_collection_log;
DROP TABLE IF EXISTS house_deal;
DROP TABLE IF EXISTS house;
DROP TABLE IF EXISTS region_code;
DROP TABLE IF EXISTS member;

CREATE TABLE member (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    email      VARCHAR(100) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    name       VARCHAR(50)  NOT NULL,
    is_admin   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE (email)
);

CREATE TABLE qna (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    member_id   BIGINT       NOT NULL,
    title       VARCHAR(200) NOT NULL,
    content     CLOB         NOT NULL,
    answer      CLOB,
    answered_at TIMESTAMP,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE region_code (
    region_code VARCHAR(10) NOT NULL,
    sido_name VARCHAR(20) NOT NULL,
    sigungu_name VARCHAR(30),
    dong_name VARCHAR(30),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (region_code)
);

CREATE TABLE house (
    id BIGINT NOT NULL AUTO_INCREMENT,
    region_code VARCHAR(10) NOT NULL,
    apt_name VARCHAR(100) NOT NULL,
    jibun VARCHAR(50) NOT NULL,
    road_address VARCHAR(100),
    build_year INT,
    house_type VARCHAR(10) NOT NULL,
    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (region_code) REFERENCES region_code(region_code)
);

CREATE TABLE house_deal (
    id BIGINT NOT NULL AUTO_INCREMENT,
    house_id BIGINT NOT NULL,
    deal_type VARCHAR(20) NOT NULL,
    deal_amount INT,
    deposit_amount INT,
    monthly_rent INT NOT NULL DEFAULT 0,
    deal_date DATE NOT NULL,
    area DECIMAL(6,2) NOT NULL,
    floor INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (house_id) REFERENCES house(id) ON DELETE CASCADE
);

CREATE TABLE batch_collection_log (
    id BIGINT NOT NULL AUTO_INCREMENT,
    job_execution_id BIGINT NOT NULL,
    job_name VARCHAR(100) NOT NULL,
    data_type VARCHAR(30) NOT NULL,
    region_code VARCHAR(10),
    year_month VARCHAR(6),
    house_type VARCHAR(20),
    deal_type VARCHAR(20),
    collected_count INT NOT NULL DEFAULT 0,
    skipped_count INT NOT NULL DEFAULT 0,
    failed_count INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL,
    started_at TIMESTAMP,
    ended_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE batch_report (
    id BIGINT NOT NULL AUTO_INCREMENT,
    job_execution_id BIGINT,
    batch_collection_log_id BIGINT,
    report_type VARCHAR(30) NOT NULL,
    source_type VARCHAR(30) NOT NULL,
    region_code VARCHAR(10),
    year_month VARCHAR(6),
    summary CLOB,
    translated_summary CLOB,
    pdf_file_name VARCHAR(255),
    pdf_file_path VARCHAR(500),
    status VARCHAR(20) NOT NULL,
    error_message VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE commercial_area (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    biz_id          VARCHAR(20)     UNIQUE,
    biz_name        VARCHAR(100)    NOT NULL,
    category_large  VARCHAR(50),
    category_medium VARCHAR(50),
    category_small  VARCHAR(50),
    latitude        DECIMAL(10, 7)  NOT NULL,
    longitude       DECIMAL(10, 7)  NOT NULL,
    address         VARCHAR(150),
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE environment_info (
    id            BIGINT          NOT NULL AUTO_INCREMENT,
    item_name     VARCHAR(100)    NOT NULL,
    `value`       DECIMAL(10, 4),
    unit          VARCHAR(20),
    measured_date DATE,
    identity_measured_date DATE GENERATED ALWAYS AS (COALESCE(measured_date, DATE '1000-01-01')),
    latitude      DECIMAL(10, 7)  NOT NULL,
    longitude     DECIMAL(10, 7)  NOT NULL,
    created_at    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE (item_name, identity_measured_date, latitude, longitude)
);

SET REFERENTIAL_INTEGRITY TRUE;
