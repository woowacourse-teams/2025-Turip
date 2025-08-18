-- V2__alter_tables.h2.sql
-- 스키마 변경: 길이 확장/축소 및 URL 칼럼 CLOB 전환(+유니크 보장)

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
    ALTER COLUMN title TYPE VARCHAR(100);
ALTER TABLE content
    ALTER COLUMN title SET NOT NULL;

ALTER TABLE content
    ALTER COLUMN url TYPE CLOB;
ALTER TABLE content
    ALTER COLUMN url SET NOT NULL;

-- 6) place
-- ⚠️ H2는 TEXT/CLOB에 UNIQUE 인덱스를 직접 걸 수 없음
--    SHA2() 함수와 STORED GENERATED COLUMN도 지원 안 함
--    → H2에서는 단순히 URL 컬럼 확장만 처리 (유니크는 생략)

-- url을 CLOB으로 확장
ALTER TABLE place
    ALTER COLUMN url TYPE CLOB;
ALTER TABLE place
    ALTER COLUMN url SET NOT NULL;

-- 7) favorite_folder
ALTER TABLE favorite_folder
    ALTER COLUMN name TYPE VARCHAR(255);
ALTER TABLE favorite_folder
    ALTER COLUMN name SET NOT NULL;
