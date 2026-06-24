INSERT INTO member (id, email, password, name, phone, is_admin) VALUES
(1, 'user@example.com', 'encoded-password', '홍길동', '010-1234-5678', FALSE);

ALTER TABLE member ALTER COLUMN id RESTART WITH 2;
