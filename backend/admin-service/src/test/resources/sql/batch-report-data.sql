INSERT INTO member (id, email, password, name, is_admin) VALUES
(1, 'admin@example.com', 'encoded-password', 'Admin', TRUE);

INSERT INTO region_code (region_code, sido_name, sigungu_name, dong_name) VALUES
('1168010100', 'Seoul', 'Gangnam-gu', 'Yeoksam-dong');

INSERT INTO house (id, region_code, apt_name, jibun, house_type, build_year) VALUES
(1, '1168010100', 'Yeoksam Raemian', '123-1', 'APARTMENT', 2010);

INSERT INTO house_deal (id, house_id, deal_type, deal_amount, deposit_amount, monthly_rent, deal_date, area, floor) VALUES
(1, 1, 'SALE', 120000, NULL, 0, '2026-06-10', 84.50, 10);

INSERT INTO batch_collection_log (
    id, job_execution_id, job_name, data_type, region_code, year_month,
    house_type, deal_type, collected_count, skipped_count, failed_count,
    status, started_at, ended_at
) VALUES (
    1, 100, 'houseDealCollectJob', 'HOUSE_DEAL', '11680', '202606',
    'APARTMENT', 'SALE', 10, 1, 0,
    'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    2, 101, 'houseDealCollectJob', 'HOUSE_DEAL', '11680', '202606',
    'APARTMENT', 'SALE', 0, 0, 1,
    'FAILED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);
