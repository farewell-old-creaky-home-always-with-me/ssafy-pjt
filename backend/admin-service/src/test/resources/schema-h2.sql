SET REFERENTIAL_INTEGRITY FALSE;

DROP TABLE IF EXISTS qna;
DROP TABLE IF EXISTS member;
DROP TABLE IF EXISTS region_code;

CREATE TABLE region_code (
    region_code  VARCHAR(10)  NOT NULL,
    sido_name    VARCHAR(50)  NOT NULL,
    sigungu_name VARCHAR(50)  NOT NULL,
    dong_name    VARCHAR(80)  NOT NULL,
    PRIMARY KEY (region_code)
);

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

SET REFERENTIAL_INTEGRITY TRUE;
