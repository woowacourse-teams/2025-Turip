-- account 테이블
CREATE TABLE account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY
);

-- guest 테이블
CREATE TABLE guest (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL UNIQUE,
    device_fid VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT fk_guest__account
       FOREIGN KEY (account_id) REFERENCES account (id)
);

-- member 테이블(기존 테이블과 이름이 겹치므로 임시적으로 member_social 이름 사용)
CREATE TABLE member_social (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   account_id BIGINT NOT NULL UNIQUE,
   provider VARCHAR(255) NOT NULL,
   provider_id VARCHAR(255) NOT NULL,
   email VARCHAR(255),
   CONSTRAINT fk_member__account
       FOREIGN KEY (account_id) REFERENCES account (id),
   CONSTRAINT uq_member__provider_provider_id
       UNIQUE (provider, provider_id)
);

-- refresh_token 테이블
CREATE TABLE refresh_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    device_fid VARCHAR(255) NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    issued_at DATETIME NOT NULL,
    expired_at DATETIME NOT NULL,
    CONSTRAINT fk_refresh_token__member
        FOREIGN KEY (member_id) REFERENCES member_social (id),
    CONSTRAINT uq_refresh_token__member_id_device_fid
        UNIQUE (member_id, device_fid)
);
