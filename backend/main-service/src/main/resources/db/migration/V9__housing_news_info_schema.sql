CREATE TABLE housing_news (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '주택 뉴스 ID',
    title VARCHAR(200) NOT NULL COMMENT '제목',
    summary TEXT COMMENT '요약',
    source_name VARCHAR(100) NOT NULL COMMENT '출처명',
    source_url VARCHAR(500) NOT NULL COMMENT '원문 URL',
    category VARCHAR(30) NOT NULL COMMENT '뉴스 분류',
    published_at DATETIME COMMENT '발행일시',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    updated_at DATETIME COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uq_housing_news_source_url (source_url),
    INDEX idx_housing_news_published (published_at, id),
    INDEX idx_housing_news_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='주택 뉴스';

CREATE TABLE housing_info (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '주택 참고 정보 ID',
    title VARCHAR(200) NOT NULL COMMENT '제목',
    content TEXT NOT NULL COMMENT '내용',
    source_name VARCHAR(100) NOT NULL COMMENT '출처명',
    source_url VARCHAR(500) NOT NULL COMMENT '원문 URL',
    info_type VARCHAR(30) NOT NULL COMMENT '정보 유형',
    published_at DATETIME COMMENT '발행일시',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    updated_at DATETIME COMMENT '수정일시',
    PRIMARY KEY (id),
    UNIQUE KEY uq_housing_info_source_url (source_url),
    INDEX idx_housing_info_published (published_at, id),
    INDEX idx_housing_info_type (info_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='주택 참고 정보';
