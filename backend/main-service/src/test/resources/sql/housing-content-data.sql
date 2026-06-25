INSERT INTO housing_news (id, title, summary, source_name, source_url, category, published_at, created_at, updated_at)
VALUES
    (1, '전세 정책 발표', '전세 정책 요약', '국토교통부', 'https://example.gov/news/1', 'POLICY', '2026-06-24 10:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, '주택 시장 동향', '시장 동향 요약', '서울시', 'https://example.gov/news/2', 'MARKET', '2026-06-25 10:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO housing_info (id, title, content, source_name, source_url, info_type, published_at, created_at, updated_at)
VALUES
    (1, '청년 주거 정책', '청년 주거 정책 내용', '국토교통부', 'https://example.gov/info/1', 'POLICY', '2026-06-24 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, '생활 편의 확인법', '생활 편의 확인 내용', '서울시', 'https://example.gov/info/2', 'LIVING', '2026-06-25 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
