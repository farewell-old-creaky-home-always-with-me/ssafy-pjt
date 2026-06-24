ALTER TABLE commercial_area
    ADD COLUMN biz_id VARCHAR(20) NULL COMMENT '상가업소번호' AFTER id,
    ADD UNIQUE INDEX uix_commercial_biz_id (biz_id);
