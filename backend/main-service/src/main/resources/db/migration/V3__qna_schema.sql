-- ============================================================
-- QnA 게시판
-- ============================================================
CREATE TABLE qna (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'QnA ID',
    member_id   BIGINT       NOT NULL COMMENT '작성자 회원 ID',
    title       VARCHAR(200) NOT NULL COMMENT '질문 제목',
    content     TEXT         NOT NULL COMMENT '질문 내용',
    answer      TEXT                  COMMENT '관리자 답변',
    answered_at DATETIME              COMMENT '답변 일시',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '작성일시',
    updated_at  DATETIME              COMMENT '수정일시',

    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member(id),
    INDEX idx_qna_member (member_id),
    INDEX idx_qna_created (created_at),
    INDEX idx_qna_answered (answered_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='QnA 게시판';
