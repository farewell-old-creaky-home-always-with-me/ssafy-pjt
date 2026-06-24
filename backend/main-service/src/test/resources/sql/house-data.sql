INSERT INTO region_code (region_code, sido_name, sigungu_name, dong_name) VALUES
('1168010100', '서울특별시', '강남구', '역삼동');

INSERT INTO house (id, region_code, apt_name, jibun, build_year, house_type, latitude, longitude) VALUES
(1, '1168010100', '역삼래미안', '757', 2005, '아파트', 37.5006130, 127.0364310),
(2, '1168010100', '역삼푸르지오', '758', 2006, '아파트', 37.5010000, 127.0370000);

INSERT INTO house_deal (id, house_id, deal_type, deal_amount, monthly_rent, deal_date, area, floor) VALUES
(1, 1, '매매', 178000, 0, '2026-05-10', 84.93, 12),
(2, 1, '전세', NULL, 0, '2026-04-18', 84.93, 9),
(3, 2, '매매', 165000, 0, '2026-05-12', 84.60, 10);

ALTER TABLE house ALTER COLUMN id RESTART WITH 3;
ALTER TABLE house_deal ALTER COLUMN id RESTART WITH 4;
