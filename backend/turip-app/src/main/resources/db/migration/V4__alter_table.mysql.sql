-- 1) favorite_order 컬럼 추가 (우선 NULL 허용)
ALTER TABLE favorite_place
    ADD COLUMN favorite_order INT NULL AFTER place_id;

-- 2) 기존 데이터에 폴더별 연속 순번 채우기
--    기준 정렬은 id 오름차순
UPDATE favorite_place AS fp
    JOIN (
    SELECT
    fp2.id,                                        -- 원본 행의 PK (업데이트 대상을 정확히 매핑하기 위한 키)
    ROW_NUMBER() OVER (
    PARTITION BY fp2.favorite_folder_id        -- 같은 폴더(favorite_folder_id)끼리 그룹핑
    ORDER BY fp2.id ASC                        -- 각 그룹(폴더) 내부에서 id 오름차순으로 정렬
    ) AS rn                                        -- 그룹별로 1,2,3,... 연속 순번 생성
    FROM favorite_place AS fp2
    ) AS x
ON x.id = fp.id                                      -- 생성한 순번(rn)을 같은 행(id 일치)에 연결
    SET fp.favorite_order = x.rn                                 -- favorite_order 컬럼에 순번 기록
WHERE fp.favorite_order IS NULL;                             -- 이미 값이 있는 경우는 덮어쓰지 않음

-- 3) NOT NULL 제약 적용
ALTER TABLE favorite_place
    MODIFY COLUMN favorite_order INT NOT NULL;
