-- 1단계) 테이블 생성
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

-- 2단계) 기존 member 데이터를 account + guest로 마이그레이션
-- 1. account 테이블에 member의 id를 그대로 사용
INSERT INTO account (id)
SELECT id FROM member;

-- 2. guest 테이블에 account_id와 device_fid 삽입
INSERT INTO guest (account_id, device_fid)
SELECT id, device_fid FROM member;

-- 3. AUTO_INCREMENT 값을 현재 최대값 + 1로 설정 (향후 INSERT 시 중복 방지)
SET @max_id = (SELECT IFNULL(MAX(id), 0) FROM account);
SET @next_id = @max_id + 1;
SET @sql = CONCAT('ALTER TABLE account AUTO_INCREMENT = ', @next_id);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3단계) favorite_content, favorite_folder의 account_id 참조 추가
-- favorite_content 테이블에 account_id 추가
ALTER TABLE favorite_content
    ADD COLUMN account_id BIGINT,
    ADD CONSTRAINT fk_favorite_content__account
        FOREIGN KEY (account_id) REFERENCES account (id);

-- favorite_content의 account_id에 member_id 값 복사
UPDATE favorite_content
SET account_id = member_id;

-- account_id를 NOT NULL로 변경
ALTER TABLE favorite_content
    MODIFY COLUMN account_id BIGINT NOT NULL;

-- favorite_folder 테이블에 account_id 추가
ALTER TABLE favorite_folder
    ADD COLUMN account_id BIGINT,
    ADD CONSTRAINT fk_favorite_folder__account
        FOREIGN KEY (account_id) REFERENCES account (id);

-- favorite_folder의 account_id에 member_id 값 복사
UPDATE favorite_folder
SET account_id = member_id;

-- account_id를 NOT NULL로 변경
ALTER TABLE favorite_folder
    MODIFY COLUMN account_id BIGINT NOT NULL;

-- 4단계) favorite_folder와 favorite_content의 member_id 컬럼 및 제약조건 삭제 & member 테이블 삭제 & member_social 테이블명 member로 변경하기
-- favorite_folder 테이블의 member_id 외래키 삭제
ALTER TABLE favorite_folder
DROP FOREIGN KEY fk_favorite_folder__member;

-- favorite_folder 테이블의 (member_id, name) unique 제약조건 삭제
ALTER TABLE favorite_folder
DROP INDEX uq_favorite_folder__member_name;

-- favorite_folder 테이블의 member_id 컬럼 삭제
ALTER TABLE favorite_folder
DROP COLUMN member_id;

-- favorite_folder 테이블의 (account_id, name) unique 제약조건 추가
ALTER TABLE favorite_folder
ADD CONSTRAINT uq_favorite_folder__account_id_name
    UNIQUE (account_id, name);

-- favorite_content 테이블의 member_id 외래키 삭제
ALTER TABLE favorite_content
DROP FOREIGN KEY fk_favorite_content__member;

-- favorite_content 테이블의 (member_id, content_id) unique 제약조건 삭제
ALTER TABLE favorite_content
DROP INDEX uq_favorite_content__member_content;

-- favorite_content 테이블의 member_id 컬럼 삭제
ALTER TABLE favorite_content
DROP COLUMN member_id;

-- favorite_content 테이블의 (account_id, content_id) unique 제약조건 추가
ALTER TABLE favorite_content
ADD CONSTRAINT uq_favorite_content__account_id_content_id
    UNIQUE (account_id, content_id);

-- member 테이블 삭제
DROP TABLE member;

-- member_social 테이블명 member로 변경하기
RENAME TABLE member_social TO member;
