-- 인덱스 생성

CREATE INDEX idx_member_deleted_at ON member (deleted_at);

CREATE INDEX idx_content_creator_id ON content (creator_id);
CREATE INDEX idx_content_city_id ON content (city_id);
CREATE INDEX idx_content_uploaded_date ON content (uploaded_date);

CREATE INDEX idx_place_name ON place (name);

CREATE INDEX idx_place_category__place_id ON place_category (place_id);
CREATE INDEX idx_place_category__category_id ON place_category (category_id);

CREATE INDEX idx_content_place__content_day ON content_place (content_id, visit_day);
CREATE INDEX idx_content_place__place ON content_place (place_id);

CREATE INDEX idx_favorite_folder__member_deleted ON favorite_folder (member_id, deleted_at);
CREATE INDEX idx_favorite_folder__deleted ON favorite_folder (deleted_at);

CREATE INDEX idx_favorite_place__folder_deleted ON favorite_place (favorite_folder_id, deleted_at);
CREATE INDEX idx_favorite_place__place_deleted ON favorite_place (place_id, deleted_at);
CREATE INDEX idx_favorite_place__deleted ON favorite_place (deleted_at);

CREATE INDEX idx_favorite_content__member_deleted ON favorite_content (member_id, deleted_at);
CREATE INDEX idx_favorite_content__content_deleted ON favorite_content (content_id, deleted_at);
CREATE INDEX idx_favorite_content__deleted ON favorite_content (deleted_at);
