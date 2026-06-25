-- SSAFY HOME mock data
-- Demo account password for all members: password

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

INSERT INTO house (id, region_code, apt_name, jibun, road_address, build_year, house_type, latitude, longitude) VALUES
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

INSERT INTO house_deal (id, house_id, deal_type, deal_amount, deposit_amount, monthly_rent, deal_date, area, floor) VALUES
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
    id,
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

INSERT INTO member (id, email, password, name, phone, is_admin) VALUES
(1, 'admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '관리자', '010-0000-0001', 1),
(2, 'user@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '김싸피', '010-0000-0002', 0),
(3, 'agent@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '박중개', '010-0000-0003', 0)
ON DUPLICATE KEY UPDATE
email = VALUES(email),
password = VALUES(password),
name = VALUES(name),
phone = VALUES(phone),
is_admin = VALUES(is_admin);

INSERT INTO favorite_area (id, member_id, region_code) VALUES
(1, 2, '1168010100'),
(2, 2, '1171010100'),
(3, 3, '1111010100')
ON DUPLICATE KEY UPDATE
member_id = VALUES(member_id),
region_code = VALUES(region_code);

INSERT INTO member_place (id, member_id, place_type, name, address, region_code, latitude, longitude) VALUES
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

INSERT INTO notice (id, member_id, title, content, created_at) VALUES
(1, 1, 'SSAFY HOME 서비스 안내', '관심 지역과 실거래가 정보를 한 화면에서 확인할 수 있습니다.', '2026-05-20 09:00:00'),
(2, 1, '목데이터 계정 안내', '데모 계정 비밀번호는 password 입니다.', '2026-05-21 10:30:00'),
(3, 1, '주택 거래 데이터 갱신', '2026년 5월 기준 목데이터가 반영되었습니다.', '2026-06-01 08:15:00')
ON DUPLICATE KEY UPDATE
member_id = VALUES(member_id),
title = VALUES(title),
content = VALUES(content),
created_at = VALUES(created_at);

INSERT INTO commercial_area (
    id,
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

INSERT INTO environment_info (id, item_name, `value`, unit, measured_date, latitude, longitude) VALUES
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

INSERT INTO facility (facility_id, name, facility_type, address, latitude, longitude) VALUES
(1, '역삼역 2번 출구', 'SUBWAY', '서울특별시 강남구 테헤란로 지하156', 37.5006220, 127.0364560),
(2, '강남파이낸스센터', 'WORK', '서울특별시 강남구 테헤란로 152', 37.5008750, 127.0354020),
(3, '역삼근린공원', 'PARK', '서울특별시 강남구 역삼동 635-1', 37.4989050, 127.0370810),
(4, '언주초등학교', 'SCHOOL', '서울특별시 강남구 남부순환로363길 19', 37.4918930, 127.0338910),
(5, '강남세브란스병원', 'HOSPITAL', '서울특별시 강남구 언주로 211', 37.4928450, 127.0462680),
(6, '개포동역', 'SUBWAY', '서울특별시 강남구 개포로 지하420', 37.4891320, 127.0661030),
(7, '잠실새내역', 'SUBWAY', '서울특별시 송파구 올림픽로 지하140', 37.5110080, 127.0863770),
(8, '청운공원 입구', 'PARK', '서울특별시 종로구 청운동', 37.5892480, 126.9694400)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
facility_type = VALUES(facility_type),
address = VALUES(address),
latitude = VALUES(latitude),
longitude = VALUES(longitude);

INSERT INTO route_request (route_request_id, member_id, house_id, place_id, total_dist_m, node_count, created_at) VALUES
(1, 2, 1, 2, 1480, 5, '2026-06-02 09:15:00'),
(2, 2, 3, 2, 6120, 6, '2026-06-02 09:21:00'),
(3, 2, 4, 2, 10450, 6, '2026-06-02 09:28:00')
ON DUPLICATE KEY UPDATE
member_id = VALUES(member_id),
house_id = VALUES(house_id),
place_id = VALUES(place_id),
total_dist_m = VALUES(total_dist_m),
node_count = VALUES(node_count),
created_at = VALUES(created_at);

INSERT INTO route_path (route_path_id, route_request_id, seq, latitude, longitude) VALUES
(1, 1, 0, 37.5006130, 127.0364310),
(2, 1, 1, 37.5006220, 127.0364560),
(3, 1, 2, 37.5007600, 127.0361200),
(4, 1, 3, 37.5008200, 127.0357600),
(5, 1, 4, 37.5008750, 127.0354020),
(6, 2, 0, 37.4886330, 127.0665640),
(7, 2, 1, 37.4891320, 127.0661030),
(8, 2, 2, 37.4928450, 127.0462680),
(9, 2, 3, 37.4989050, 127.0370810),
(10, 2, 4, 37.5006220, 127.0364560),
(11, 2, 5, 37.5008750, 127.0354020),
(12, 3, 0, 37.5127170, 127.0823660),
(13, 3, 1, 37.5110080, 127.0863770),
(14, 3, 2, 37.5053000, 127.0735000),
(15, 3, 3, 37.5019000, 127.0502000),
(16, 3, 4, 37.5006220, 127.0364560),
(17, 3, 5, 37.5008750, 127.0354020)
ON DUPLICATE KEY UPDATE
route_request_id = VALUES(route_request_id),
seq = VALUES(seq),
latitude = VALUES(latitude),
longitude = VALUES(longitude);

INSERT INTO neighborhood_demographics (
    id,
    sido_name,
    sigungu_name,
    dong_name,
    total_population,
    household_count,
    senior_count,
    foreign_count,
    reference_date
) VALUES
(1, '서울특별시', '강남구', '역삼동', 35218, 18942, 4210, 1785, '202605'),
(2, '서울특별시', '강남구', '개포동', 28410, 11203, 3821, 624, '202605'),
(3, '서울특별시', '강남구', '삼성동', 23190, 10577, 3014, 1192, '202605'),
(4, '서울특별시', '송파구', '잠실동', 31884, 14230, 3766, 892, '202605'),
(5, '서울특별시', '종로구', '청운동', 10820, 4628, 2217, 318, '202605')
ON DUPLICATE KEY UPDATE
total_population = VALUES(total_population),
household_count = VALUES(household_count),
senior_count = VALUES(senior_count),
foreign_count = VALUES(foreign_count),
reference_date = VALUES(reference_date);

INSERT INTO cctv_info (id, purpose, camera_count, address, latitude, longitude) VALUES
(1, '생활방범', 4, '서울특별시 강남구 역삼로 306 인근', 37.5004200, 127.0367000),
(2, '어린이보호', 3, '서울특별시 강남구 남부순환로363길 19', 37.4918930, 127.0338910),
(3, '생활방범', 6, '서울특별시 강남구 삼성로 14 인근', 37.4889000, 127.0662000),
(4, '교통단속', 5, '서울특별시 송파구 올림픽로 99 인근', 37.5125000, 127.0827000),
(5, '생활방범', 2, '서울특별시 종로구 자하문로 125 인근', 37.5895200, 126.9697600)
ON DUPLICATE KEY UPDATE
purpose = VALUES(purpose),
camera_count = VALUES(camera_count),
address = VALUES(address),
latitude = VALUES(latitude),
longitude = VALUES(longitude);

INSERT INTO housing_news (
    id,
    title,
    summary,
    source_name,
    source_url,
    category,
    published_at
) VALUES
(1, '강남권 아파트 매매 거래, 역세권 중심으로 회복세', '역삼동과 삼성동 주요 단지를 중심으로 5월 실거래 신고가 늘었습니다.', 'SSAFY 데모뉴스', 'https://example.com/news/ssafy-home-001', 'MARKET', '2026-06-03 08:00:00'),
(2, '전월세 수요, 직주근접 지역 선호 뚜렷', '회사와 가까운 생활권을 기준으로 매물을 비교하는 수요가 증가했습니다.', 'SSAFY 데모뉴스', 'https://example.com/news/ssafy-home-002', 'RENT', '2026-06-02 09:30:00'),
(3, '주거 선택 기준, 가격에서 생활 인프라로 확장', '상권, 공원, 교통 접근성 등 주변 데이터가 의사결정에 중요한 요소로 떠올랐습니다.', 'SSAFY 데모뉴스', 'https://example.com/news/ssafy-home-003', 'LIFE', '2026-06-01 10:00:00')
ON DUPLICATE KEY UPDATE
title = VALUES(title),
summary = VALUES(summary),
source_name = VALUES(source_name),
source_url = VALUES(source_url),
category = VALUES(category),
published_at = VALUES(published_at);

INSERT INTO housing_info (
    id,
    title,
    content,
    source_name,
    source_url,
    info_type,
    published_at
) VALUES
(1, '실거래가를 볼 때 확인할 항목', '실거래가는 광고 호가와 다르게 실제 신고된 거래 가격입니다. 같은 단지라도 면적, 층수, 거래일에 따라 가격 차이가 크므로 함께 비교해야 합니다.', 'SSAFY HOME 가이드', 'https://example.com/info/actual-price-guide', 'GUIDE', '2026-06-01 09:00:00'),
(2, '직주근접 매물 비교 방법', '통근 시간은 매달 반복되는 비용입니다. 총 이동 거리와 환승 부담을 함께 확인하면 가격만으로 보이지 않는 생활 비용을 줄일 수 있습니다.', 'SSAFY HOME 가이드', 'https://example.com/info/commute-guide', 'GUIDE', '2026-06-01 09:10:00'),
(3, 'AI 리포트 활용법', '배치로 수집된 거래 데이터를 AI 요약과 PDF로 확인하면 긴 표를 읽지 않아도 지역별 시장 흐름을 빠르게 파악할 수 있습니다.', 'SSAFY HOME 가이드', 'https://example.com/info/ai-report-guide', 'AI_REPORT', '2026-06-01 09:20:00')
ON DUPLICATE KEY UPDATE
title = VALUES(title),
content = VALUES(content),
source_name = VALUES(source_name),
source_url = VALUES(source_url),
info_type = VALUES(info_type),
published_at = VALUES(published_at);

INSERT INTO batch_report (
    id,
    job_execution_id,
    batch_collection_log_id,
    report_type,
    source_type,
    region_code,
    `year_month`,
    summary,
    translated_summary,
    pdf_file_name,
    pdf_file_path,
    status,
    created_at,
    updated_at
) VALUES
(1, 2001, 1, 'MONTHLY_MARKET_SUMMARY', 'HOUSE_DEAL', '1168010100', '202605',
'2026년 5월 강남구 역삼동 아파트 매매 데이터는 역세권 신축 및 준신축 단지를 중심으로 높은 가격대를 유지했습니다. 역삼래미안은 전용 84.93㎡가 17억 8천만 원에 거래되었고, 강남센트럴아이파크는 전용 59.97㎡가 22억 1천만 원에 거래되어 면적 대비 높은 선호도를 보였습니다. 직주근접 수요가 강하고 생활 편의시설 접근성이 좋아 단기적으로 가격 하방 압력은 제한적입니다.',
'In May 2026, apartment sales in Yeoksam-dong, Gangnam-gu remained strong around station-adjacent newer complexes. Yeoksam Raemian traded at KRW 1.78 billion for 84.93㎡, while Gangnam Central IPARK traded at KRW 2.21 billion for 59.97㎡, showing strong demand despite smaller unit size. Demand from commute-friendly households and convenient local infrastructure may limit near-term downside pressure.',
'gangnam-yeoksam-202605-report.pdf',
'/tmp/ssafy-home/reports/batch/gangnam-yeoksam-202605-report.pdf',
'PDF_COMPLETED',
'2026-06-02 03:10:00',
'2026-06-02 03:12:30')
ON DUPLICATE KEY UPDATE
job_execution_id = VALUES(job_execution_id),
batch_collection_log_id = VALUES(batch_collection_log_id),
report_type = VALUES(report_type),
source_type = VALUES(source_type),
region_code = VALUES(region_code),
`year_month` = VALUES(`year_month`),
summary = VALUES(summary),
translated_summary = VALUES(translated_summary),
pdf_file_name = VALUES(pdf_file_name),
pdf_file_path = VALUES(pdf_file_path),
status = VALUES(status),
created_at = VALUES(created_at),
updated_at = VALUES(updated_at);
