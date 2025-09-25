-- 기존 FK 삭제
ALTER TABLE favorite_place
DROP FOREIGN KEY fk_favorite_place__folder;

-- 새 FK 추가 (ON DELETE CASCADE 옵션 포함)
ALTER TABLE favorite_place
ADD CONSTRAINT fk_favorite_place__folder
FOREIGN KEY (favorite_folder_id) REFERENCES favorite_folder(id) ON DELETE CASCADE;
