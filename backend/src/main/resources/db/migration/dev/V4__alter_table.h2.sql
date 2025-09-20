-- 1) 컬럼 추가
ALTER TABLE favorite_place
    ADD COLUMN "position" INT;

-- 2) 기존 데이터에 폴더별 연속 순번 채우기
UPDATE favorite_place fp
SET "position" = (
    SELECT rn
    FROM (
             SELECT id,
                    ROW_NUMBER() OVER (
                   PARTITION BY favorite_folder_id
                   ORDER BY id
               ) AS rn
             FROM favorite_place
         ) x
    WHERE x.id = fp.id
)
WHERE fp."position" IS NULL;

-- 3) NOT NULL 제약 적용
ALTER TABLE favorite_place
    ALTER COLUMN "position" SET NOT NULL;
