-- ============================================================
-- Board
-- ============================================================
CREATE TABLE board (
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Board ID',
    member_id  BIGINT       NOT NULL COMMENT 'Author member ID',
    title      VARCHAR(200) NOT NULL COMMENT 'Title',
    content    TEXT         NOT NULL COMMENT 'Content',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created at',
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Updated at',

    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member(id),
    INDEX idx_board_member (member_id),
    INDEX idx_board_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Board';
