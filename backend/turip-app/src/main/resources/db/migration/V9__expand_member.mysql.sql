-- 자체 로그인 도입을 위한 테이블 구조 변경

-- expand) social_member 테이블 추가
CREATE TABLE social_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    provider VARCHAR(255) NOT NULL,
    provider_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_social_member__member
        FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    CONSTRAINT uq_social_member__provider_provider_id
        UNIQUE (provider, provider_id)
);

-- expand) turip_member 테이블 추가
CREATE TABLE turip_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    login_id VARCHAR(255) NOT NULL UNIQUE,
    login_password VARCHAR(255) NOT NULL,
    CONSTRAINT fk_turip_member__member
        FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

-- expand) 사용하지 않는 필드 nullable 수정
ALTER TABLE member
    MODIFY COLUMN provider VARCHAR(255) NULL,
    MODIFY COLUMN provider_id VARCHAR(255) NULL;

-- expand) role 필드 추가
ALTER TABLE account ADD COLUMN role VARCHAR(255) NOT NULL DEFAULT 'USER';

-- expand) is_first_login 필드 추가
ALTER TABLE member ADD COLUMN is_first_login BOOLEAN NOT NULL DEFAULT true;

-- modify) email 필드에 not null 속성 추가
UPDATE member SET email = 'unknown@turip.com' WHERE email IS NULL;
ALTER TABLE member MODIFY COLUMN email VARCHAR(255) NOT NULL;

-- backfill) 기존 데이터 복사 및 변경
INSERT INTO social_member (member_id, provider, provider_id)
SELECT id, provider, provider_id from member;

UPDATE account SET role = 'USER';
UPDATE member SET is_first_login = false;
