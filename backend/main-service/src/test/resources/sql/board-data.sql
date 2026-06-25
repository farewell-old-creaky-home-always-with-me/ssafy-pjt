INSERT INTO member (id, email, password, name, phone, is_admin) VALUES
(1, 'user@example.com', 'encoded-password', 'User One', '01011112222', FALSE),
(2, 'other@example.com', 'encoded-password', 'User Two', '01022223333', FALSE),
(3, 'admin@example.com', 'encoded-password', 'Admin User', '01033334444', TRUE);

INSERT INTO board (id, member_id, title, content, created_at) VALUES
(1, 1, 'First board', 'First board content', TIMESTAMP '2026-06-01 10:00:00'),
(2, 2, 'Second board', 'Second board content', TIMESTAMP '2026-06-02 10:00:00');

ALTER TABLE member ALTER COLUMN id RESTART WITH 4;
ALTER TABLE board ALTER COLUMN id RESTART WITH 3;
