UPDATE member
SET phone = REGEXP_REPLACE(phone, '[[:space:]-]', '')
WHERE phone IS NOT NULL;

ALTER TABLE member
    MODIFY phone VARCHAR(20) NOT NULL COMMENT '전화번호';
