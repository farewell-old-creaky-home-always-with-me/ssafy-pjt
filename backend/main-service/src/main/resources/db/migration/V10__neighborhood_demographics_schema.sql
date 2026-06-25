CREATE TABLE neighborhood_demographics (
    id               BIGINT      NOT NULL AUTO_INCREMENT COMMENT '인구 통계 ID',
    sido_name        VARCHAR(20) NOT NULL COMMENT '시도명',
    sigungu_name     VARCHAR(30) NOT NULL COMMENT '시군구명',
    dong_name        VARCHAR(30) NOT NULL COMMENT '행정동명',
    total_population INT                  COMMENT '총 인구수',
    household_count  INT                  COMMENT '세대수',
    senior_count     INT                  COMMENT '65세 이상 고령자 수',
    foreign_count    INT                  COMMENT '외국인 등록 수',
    reference_date   VARCHAR(6)  NOT NULL COMMENT '기준 연월 (YYYYMM)',
    created_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME             ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_demographics_identity (sido_name, sigungu_name, dong_name, reference_date),
    INDEX idx_demographics_location (sido_name, sigungu_name, dong_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='동네 구성원 통계';
