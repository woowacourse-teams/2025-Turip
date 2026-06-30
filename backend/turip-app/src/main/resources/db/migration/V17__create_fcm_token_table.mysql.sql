CREATE TABLE fcm_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    CONSTRAINT uk_fcm_token_token UNIQUE (token),
    CONSTRAINT fk_fcm_token__account
        FOREIGN KEY (account_id) REFERENCES account (id)
);
