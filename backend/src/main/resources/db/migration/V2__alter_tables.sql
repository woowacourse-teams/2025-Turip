-- V2__alter_tables.sql
-- 스키마 변경: 길이 확장/축소 및 URL 칼럼 TEXT 전환(+유니크 보장)

-- 1) country
-- name 100 -> 255 (기존 UNIQUE 인덱스는 자동 유지되므로 UNIQUE 재명시 불필요)
ALTER TABLE country
    MODIFY name VARCHAR(255) NOT NULL;

-- image_url 255 -> TEXT
ALTER TABLE country
    MODIFY image_url TEXT NOT NULL;

-- 2) city
-- image_url 255 -> TEXT
ALTER TABLE city
    MODIFY image_url TEXT NOT NULL;

-- 3) creator
-- channel_name 255 -> 50 (길이 축소: 50 초과 데이터가 있으면 실패합니다)
ALTER TABLE creator
    MODIFY channel_name VARCHAR(50) NOT NULL;

-- profile_image 255 -> TEXT
ALTER TABLE creator
    MODIFY profile_image TEXT NOT NULL;

-- 4) content
-- title 255 -> 100 (길이 축소: 100 초과 데이터가 있으면 실패합니다)
ALTER TABLE content
    MODIFY title VARCHAR(100) NOT NULL;

-- url 255 -> TEXT
ALTER TABLE content
    MODIFY url TEXT NOT NULL;

-- 5) place
-- 기존 UNIQUE(url) 인덱스 제거
ALTER TABLE place
    DROP INDEX url;

-- url을 TEXT로 확장
ALTER TABLE place
    MODIFY url TEXT NOT NULL;

-- url 해시 생성 칼럼 추가 (저장형 생성 칼럼)
ALTER TABLE place
    ADD COLUMN url_sha CHAR(64) AS (SHA2(url, 256)) STORED;

-- 해시에 UNIQUE 인덱스 부여
-- 인덱스 이름을 'url'로 지정하면, 기존 이름을 유지하는 효과를 줄 수 있습니다.
ALTER TABLE place
    ADD UNIQUE KEY url (url_sha);

-- 6) favorite_folder
-- name 100 -> 255 (복합 UNIQUE(member_id, name)은 자동 유지)
ALTER TABLE favorite_folder
    MODIFY name VARCHAR(255) NOT NULL;
