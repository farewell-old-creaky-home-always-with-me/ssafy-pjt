CREATE TABLE cctv_info (
    id           BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'CCTV 정보 ID',
    purpose      VARCHAR(100)  NOT NULL COMMENT 'CCTV 설치 목적',
    camera_count INT                    COMMENT '카메라 대수',
    address      VARCHAR(200)           COMMENT '설치 주소',
    latitude     DECIMAL(10,7) NOT NULL COMMENT '위도',
    longitude    DECIMAL(10,7) NOT NULL COMMENT '경도',
    created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '데이터 등록일시',
    updated_at   DATETIME               ON UPDATE CURRENT_TIMESTAMP COMMENT '데이터 수정일시',

    PRIMARY KEY (id),
    UNIQUE KEY uq_cctv_identity (latitude, longitude, purpose),
    INDEX idx_cctv_location (latitude, longitude)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CCTV 설치 현황';
