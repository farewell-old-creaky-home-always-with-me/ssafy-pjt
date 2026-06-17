SET REFERENTIAL_INTEGRITY FALSE;

CREATE TABLE IF NOT EXISTS region_code (
    region_code  VARCHAR(10)  NOT NULL,
    sido_name    VARCHAR(20)  NOT NULL,
    sigungu_name VARCHAR(30),
    dong_name    VARCHAR(30),
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (region_code)
);

CREATE TABLE IF NOT EXISTS member (
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

CREATE TABLE IF NOT EXISTS favorite_area (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    member_id   BIGINT      NOT NULL,
    region_code VARCHAR(10) NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    FOREIGN KEY (region_code) REFERENCES region_code(region_code),
    UNIQUE (member_id, region_code)
);

CREATE TABLE IF NOT EXISTS house (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    region_code  VARCHAR(10)  NOT NULL,
    apt_name     VARCHAR(100) NOT NULL,
    jibun        VARCHAR(50)  NOT NULL,
    road_address VARCHAR(100),
    build_year   INT,
    house_type   VARCHAR(10)  NOT NULL,
    latitude     DECIMAL(10,7),
    longitude    DECIMAL(10,7),
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (region_code) REFERENCES region_code(region_code),
    UNIQUE (region_code, apt_name, jibun, house_type)
);

CREATE TABLE IF NOT EXISTS house_deal (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    house_id       BIGINT       NOT NULL,
    deal_type      VARCHAR(20)  NOT NULL,
    deal_amount    INT,
    deposit_amount INT,
    monthly_rent   INT          NOT NULL DEFAULT 0,
    deal_date      DATE         NOT NULL,
    area           DECIMAL(6,2) NOT NULL,
    floor          INT,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (house_id) REFERENCES house(id) ON DELETE CASCADE
);

SET REFERENTIAL_INTEGRITY TRUE;
