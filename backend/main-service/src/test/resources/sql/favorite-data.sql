INSERT INTO region_code (region_code, sido_name, sigungu_name, dong_name) VALUES
('1168010100', '서울특별시', '강남구', '역삼동'),
('1171010100', '서울특별시', '송파구', '잠실동');

INSERT INTO member (id, email, password, name, phone, is_admin) VALUES
(1, 'user@example.com', 'encoded-password', '김싸피', '01011112222', FALSE);

INSERT INTO favorite_area (id, member_id, region_code) VALUES
(1, 1, '1168010100');

ALTER TABLE member ALTER COLUMN id RESTART WITH 2;
ALTER TABLE favorite_area ALTER COLUMN id RESTART WITH 2;
