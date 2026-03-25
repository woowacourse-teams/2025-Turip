-- content_pending 테이블 생성
CREATE TABLE content_pending (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content_data JSON NOT NULL,
    status VARCHAR(255) NOT NULL DEFAULT 'PENDING',
    collector_account_id BIGINT NOT NULL,
    validator_account_id BIGINT NULL,
    reject_reason TEXT NULL,
    content_id BIGINT NULL,
    content_data_updated_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,

    CONSTRAINT fk_content_pending__collector_account
        FOREIGN KEY (collector_account_id) REFERENCES account (id),
    CONSTRAINT fk_content_pending__validator_account
        FOREIGN KEY (validator_account_id) REFERENCES account (id),
    CONSTRAINT fk_content_pending__content
        FOREIGN KEY (content_id) REFERENCES content (id)
);
