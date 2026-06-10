-- SSAFY HOME mock data
-- Demo account password for all members: password

SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO region_code (region_code, sido_name, sigungu_name, dong_name) VALUES
('1111010100', '서울특별시', '종로구', '청운동'),
('1111010200', '서울특별시', '종로구', '신교동'),
('1111010300', '서울특별시', '종로구', '궁정동'),
('1168010100', '서울특별시', '강남구', '역삼동'),
('1168010300', '서울특별시', '강남구', '개포동'),
('1168010500', '서울특별시', '강남구', '삼성동'),
('1171010100', '서울특별시', '송파구', '잠실동')
ON DUPLICATE KEY UPDATE
sido_name = VALUES(sido_name),
sigungu_name = VALUES(sigungu_name),
dong_name = VALUES(dong_name);

INSERT INTO house (house_id, region_code, apt_name, jibun, road_address, build_year, house_type, latitude, longitude) VALUES
(1, '1168010100', '역삼래미안', '757', '서울특별시 강남구 역삼로 306', 2005, '아파트', 37.5006130, 127.0364310),
(2, '1168010100', '강남센트럴아이파크', '712-3', '서울특별시 강남구 테헤란로 152', 2022, '아파트', 37.5008750, 127.0354020),
(3, '1168010300', '개포자이프레지던스', '189', '서울특별시 강남구 삼성로 14', 2023, '아파트', 37.4886330, 127.0665640),
(4, '1171010100', '잠실엘스', '19', '서울특별시 송파구 올림픽로 99', 2008, '아파트', 37.5127170, 127.0823660),
(5, '1111010100', '청운빌라', '52-1', '서울특별시 종로구 자하문로 125', 2012, '다세대', 37.5897430, 126.9698290),
(6, '1111010200', '신교하우스', '7-4', '서울특별시 종로구 필운대로 96', 2018, '다세대', 37.5842440, 126.9708620)
ON DUPLICATE KEY UPDATE
region_code = VALUES(region_code),
apt_name = VALUES(apt_name),
jibun = VALUES(jibun),
road_address = VALUES(road_address),
build_year = VALUES(build_year),
house_type = VALUES(house_type),
latitude = VALUES(latitude),
longitude = VALUES(longitude);

INSERT INTO house_deal (deal_id, house_id, deal_type, deal_amount, deposit_amount, monthly_rent, deal_date, area, floor) VALUES
(1, 1, '매매', 178000, NULL, 0, '2026-05-10', 84.93, 12),
(2, 1, '전세', NULL, 92000, 0, '2026-04-18', 84.93, 9),
(3, 2, '매매', 221000, NULL, 0, '2026-05-21', 59.97, 18),
(4, 2, '월세', NULL, 30000, 180, '2026-03-11', 59.97, 7),
(5, 3, '매매', 245000, NULL, 0, '2026-05-08', 84.99, 21),
(6, 3, '전세', NULL, 120000, 0, '2026-02-24', 84.99, 16),
(7, 4, '매매', 199000, NULL, 0, '2026-05-16', 84.80, 14),
(8, 4, '월세', NULL, 50000, 220, '2026-04-02', 84.80, 6),
(9, 5, '전세', NULL, 38000, 0, '2026-05-03', 48.12, 3),
(10, 5, '매매', 72000, NULL, 0, '2026-01-19', 48.12, 2),
(11, 6, '월세', NULL, 12000, 75, '2026-05-27', 42.31, 4),
(12, 6, '전세', NULL, 31000, 0, '2026-03-29', 42.31, 2)
ON DUPLICATE KEY UPDATE
house_id = VALUES(house_id),
deal_type = VALUES(deal_type),
deal_amount = VALUES(deal_amount),
deposit_amount = VALUES(deposit_amount),
monthly_rent = VALUES(monthly_rent),
deal_date = VALUES(deal_date),
area = VALUES(area),
floor = VALUES(floor);

INSERT INTO batch_collection_log (
    collection_log_id,
    job_execution_id,
    job_name,
    data_type,
    region_code,
    `year_month`,
    house_type,
    deal_type,
    collected_count,
    skipped_count,
    failed_count,
    status,
    started_at,
    ended_at
) VALUES
(1, 1001, 'mockHouseDealCollectionJob', 'HOUSE_DEAL', '1168010100', '202605', '아파트', '매매', 4, 0, 0, 'COMPLETED', '2026-06-01 02:00:00', '2026-06-01 02:02:13')
ON DUPLICATE KEY UPDATE
job_execution_id = VALUES(job_execution_id),
job_name = VALUES(job_name),
data_type = VALUES(data_type),
region_code = VALUES(region_code),
`year_month` = VALUES(`year_month`),
house_type = VALUES(house_type),
deal_type = VALUES(deal_type),
collected_count = VALUES(collected_count),
skipped_count = VALUES(skipped_count),
failed_count = VALUES(failed_count),
status = VALUES(status),
started_at = VALUES(started_at),
ended_at = VALUES(ended_at);

INSERT INTO member (member_id, email, password, name, is_admin) VALUES
(1, 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '관리자', 1),
(2, 'user@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '김싸피', 0),
(3, 'agent@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '박중개', 0)
ON DUPLICATE KEY UPDATE
email = VALUES(email),
password = VALUES(password),
name = VALUES(name),
is_admin = VALUES(is_admin);

INSERT INTO favorite_area (favorite_id, member_id, region_code) VALUES
(1, 2, '1168010100'),
(2, 2, '1171010100'),
(3, 3, '1111010100')
ON DUPLICATE KEY UPDATE
member_id = VALUES(member_id),
region_code = VALUES(region_code);

INSERT INTO member_place (place_id, member_id, place_type, name, address, region_code, latitude, longitude) VALUES
(1, 2, 'HOME', '우리집', '서울특별시 강남구 역삼로 306', '1168010100', 37.5006130, 127.0364310),
(2, 2, 'WORK', '회사', '서울특별시 강남구 테헤란로 152', '1168010100', 37.5008750, 127.0354020),
(3, 3, 'OTHER', '상담 예정지', '서울특별시 종로구 자하문로 125', '1111010100', 37.5897430, 126.9698290)
ON DUPLICATE KEY UPDATE
member_id = VALUES(member_id),
place_type = VALUES(place_type),
name = VALUES(name),
address = VALUES(address),
region_code = VALUES(region_code),
latitude = VALUES(latitude),
longitude = VALUES(longitude);

INSERT INTO notice (notice_id, member_id, title, content, created_at) VALUES
(1, 1, 'SSAFY HOME 서비스 안내', '관심 지역과 실거래가 정보를 한 화면에서 확인할 수 있습니다.', '2026-05-20 09:00:00'),
(2, 1, '목데이터 계정 안내', '데모 계정 비밀번호는 password 입니다.', '2026-05-21 10:30:00'),
(3, 1, '주택 거래 데이터 갱신', '2026년 5월 기준 목데이터가 반영되었습니다.', '2026-06-01 08:15:00')
ON DUPLICATE KEY UPDATE
member_id = VALUES(member_id),
title = VALUES(title),
content = VALUES(content),
created_at = VALUES(created_at);

INSERT INTO commercial_area (
    commercial_id,
    biz_name,
    category_large,
    category_medium,
    category_small,
    latitude,
    longitude,
    address
) VALUES
(1, '역삼 브런치카페', '음식', '카페', '브런치카페', 37.5009910, 127.0369200, '서울특별시 강남구 역삼로 310'),
(2, '테헤란 약국', '소매', '의약품', '약국', 37.5011220, 127.0347210, '서울특별시 강남구 테헤란로 150'),
(3, '개포 학원가 독서실', '교육', '학원', '독서실', 37.4889220, 127.0662410, '서울특별시 강남구 삼성로 16'),
(4, '잠실 생활마트', '소매', '종합소매', '슈퍼마켓', 37.5124020, 127.0819530, '서울특별시 송파구 올림픽로 101'),
(5, '청운동 세탁소', '생활서비스', '세탁', '세탁소', 37.5895210, 126.9703220, '서울특별시 종로구 자하문로 129')
ON DUPLICATE KEY UPDATE
biz_name = VALUES(biz_name),
category_large = VALUES(category_large),
category_medium = VALUES(category_medium),
category_small = VALUES(category_small),
latitude = VALUES(latitude),
longitude = VALUES(longitude),
address = VALUES(address);

INSERT INTO environment_info (env_id, item_name, `value`, unit, measured_date, latitude, longitude) VALUES
(1, '초미세먼지', 18.0000, 'ug/m3', '2026-05-30', 37.5006130, 127.0364310),
(2, '소음도', 54.2000, 'dB', '2026-05-30', 37.5008750, 127.0354020),
(3, '녹지 접근성', 82.5000, 'score', '2026-05-30', 37.4886330, 127.0665640),
(4, '초미세먼지', 16.3000, 'ug/m3', '2026-05-30', 37.5127170, 127.0823660),
(5, '소음도', 48.7000, 'dB', '2026-05-30', 37.5897430, 126.9698290)
ON DUPLICATE KEY UPDATE
item_name = VALUES(item_name),
`value` = VALUES(`value`),
unit = VALUES(unit),
measured_date = VALUES(measured_date),
latitude = VALUES(latitude),
longitude = VALUES(longitude);

INSERT INTO route_node (node_id, latitude, longitude, node_name) VALUES
(1, 37.5006130, 127.0364310, '역삼래미안 앞'),
(2, 37.5008750, 127.0354020, '테헤란로 교차로'),
(3, 37.5011220, 127.0347210, '역삼역 방면'),
(4, 37.4886330, 127.0665640, '개포자이프레지던스 앞'),
(5, 37.5127170, 127.0823660, '잠실엘스 앞')
ON DUPLICATE KEY UPDATE
latitude = VALUES(latitude),
longitude = VALUES(longitude),
node_name = VALUES(node_name);

INSERT INTO route_edge (edge_id, from_node_id, to_node_id, distance) VALUES
(1, 1, 2, 96.5),
(2, 2, 1, 96.5),
(3, 2, 3, 71.8),
(4, 3, 2, 71.8),
(5, 4, 5, 2750.0),
(6, 5, 4, 2750.0)
ON DUPLICATE KEY UPDATE
from_node_id = VALUES(from_node_id),
to_node_id = VALUES(to_node_id),
distance = VALUES(distance);

SET FOREIGN_KEY_CHECKS = 1;
