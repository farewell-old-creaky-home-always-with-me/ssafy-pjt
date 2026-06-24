CREATE TABLE batch_report (
    id                      BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Batch report ID',
    job_execution_id        BIGINT                COMMENT 'Report generation job execution ID',
    batch_collection_log_id BIGINT                COMMENT 'Source batch collection log ID',
    report_type             VARCHAR(30)  NOT NULL COMMENT 'Report type',
    source_type             VARCHAR(30)  NOT NULL COMMENT 'Source data type',
    region_code             VARCHAR(10)           COMMENT 'Region code',
    `year_month`            VARCHAR(6)            COMMENT 'Collection year month',
    summary                 TEXT                  COMMENT 'AI summary result',
    translated_summary      TEXT                  COMMENT 'AI translation result',
    pdf_file_name           VARCHAR(255)          COMMENT 'PDF file name',
    pdf_file_path           VARCHAR(500)          COMMENT 'PDF file path',
    status                  VARCHAR(20)  NOT NULL COMMENT 'Report generation status',
    error_message           VARCHAR(1000)         COMMENT 'Failure message',
    created_at              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created timestamp',
    updated_at              DATETIME              COMMENT 'Updated timestamp',

    PRIMARY KEY (id),
    INDEX idx_batch_report_job_execution (job_execution_id),
    INDEX idx_batch_report_collection_log (batch_collection_log_id),
    INDEX idx_batch_report_condition (region_code, `year_month`, source_type),
    INDEX idx_batch_report_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Batch AI/PDF report';
