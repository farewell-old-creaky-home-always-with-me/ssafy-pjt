INSERT INTO member (id, email, password, name, phone, is_admin) VALUES
(1, 'user@example.com', 'encoded-password', 'User One', '01011112222', FALSE),
(2, 'other@example.com', 'encoded-password', 'User Two', '01022223333', FALSE);

INSERT INTO qna (id, member_id, title, content, answer, answered_at, created_at) VALUES
(1, 1, 'First question', 'First question content', NULL, NULL, TIMESTAMP '2026-06-01 10:00:00'),
(2, 2, 'Answered question', 'Answered question content', 'Admin answer', TIMESTAMP '2026-06-02 10:00:00', TIMESTAMP '2026-06-02 09:00:00');

ALTER TABLE member ALTER COLUMN id RESTART WITH 3;
ALTER TABLE qna ALTER COLUMN id RESTART WITH 3;
