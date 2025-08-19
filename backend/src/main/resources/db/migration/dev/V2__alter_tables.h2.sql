-- V2__alter_tables.h2.sql
-- 스키마 변경: H2 제약에 맞게 TEXT→CLOB, url은 VARCHAR(2048)+UNIQUE로 타협

-- 1) country
ALTER TABLE country
    ALTER COLUMN name TYPE VARCHAR(255);
ALTER TABLE country
    ALTER COLUMN name SET NOT NULL;

ALTER TABLE country
    ALTER COLUMN image_url TYPE CLOB;
ALTER TABLE country
    ALTER COLUMN image_url SET NOT NULL;

-- 2) province
ALTER TABLE province
    ADD CONSTRAINT uq_province_name UNIQUE (name);

-- 3) city
ALTER TABLE city
    ALTER COLUMN image_url TYPE CLOB;
ALTER TABLE city
    ALTER COLUMN image_url SET NOT NULL;

-- 4) creator
ALTER TABLE creator
    ALTER COLUMN channel_name TYPE VARCHAR(50);
ALTER TABLE creator
    ALTER COLUMN channel_name SET NOT NULL;

ALTER TABLE creator
    ALTER COLUMN profile_image TYPE CLOB;
ALTER TABLE creator
    ALTER COLUMN profile_image SET NOT NULL;

-- 5) content
ALTER TABLE content
    ALTER COLUMN title TYPE VARCHAR(500);
ALTER TABLE content
    ALTER COLUMN title SET NOT NULL;

ALTER TABLE content
    ALTER COLUMN url TYPE CLOB;
ALTER TABLE content
    ALTER COLUMN url SET NOT NULL;

-- 6) place
-- MySQL은 TEXT+SHA2 해시, H2는 VARCHAR+UNIQUE로 대체
DROP INDEX IF EXISTS url;

ALTER TABLE place
    ALTER COLUMN url TYPE VARCHAR(2048);
ALTER TABLE place
    ALTER COLUMN url SET NOT NULL;

ALTER TABLE place
    ADD CONSTRAINT uq_place_url UNIQUE (url);

-- 7) favorite_folder
ALTER TABLE favorite_folder
    ALTER COLUMN name TYPE VARCHAR(255);
ALTER TABLE favorite_folder
    ALTER COLUMN name SET NOT NULL;
