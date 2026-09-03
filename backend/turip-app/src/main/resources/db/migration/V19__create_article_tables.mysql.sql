-- article 테이블 생성
CREATE TABLE article (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    subtitle VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    thumbnail_url VARCHAR(255) NULL,
    author_id BIGINT NULL,
    display_order INT NOT NULL,
    is_published BOOLEAN NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,

    CONSTRAINT fk_article__account
        FOREIGN KEY (author_id) REFERENCES account (id)
            ON DELETE SET NULL
);

-- tag 테이블 생성
CREATE TABLE tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL,

    CONSTRAINT uq_tag__name UNIQUE (name)
);

-- article_tag 테이블 생성 (article - tag 다대다 연결)
CREATE TABLE article_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,

    CONSTRAINT fk_article_tag__article
        FOREIGN KEY (article_id) REFERENCES article (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_article_tag__tag
        FOREIGN KEY (tag_id) REFERENCES tag (id),

    CONSTRAINT uq_article_tag__article_id_tag_id
        UNIQUE (article_id, tag_id)
);

-- article_place 테이블 생성 (article - place 다대다 연결)
CREATE TABLE article_place (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id BIGINT NOT NULL,
    place_id BIGINT NOT NULL,

    CONSTRAINT fk_article_place__article
        FOREIGN KEY (article_id) REFERENCES article (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_article_place__place
        FOREIGN KEY (place_id) REFERENCES place (id),

    CONSTRAINT uq_article_place__article_id_place_id
        UNIQUE (article_id, place_id)
);
