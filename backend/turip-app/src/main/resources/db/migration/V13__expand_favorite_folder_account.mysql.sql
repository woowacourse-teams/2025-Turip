-- 공유찜폴더 기능 추가에 따른 설계 변경

-- 1. account 테이블에 nickname 필드 추가
ALTER TABLE account
    ADD COLUMN nickname VARCHAR(255) UNIQUE;

-- 2. 기존 account의 nickname 업데이트 (랜덤 닉네임 생성)
UPDATE account
SET nickname = CONCAT(
    -- FIRST 카테고리 (동작)
    ELT(FLOOR(1 + RAND() * 10),
        '여행하는', '코딩하는', '달리는', '걷는', '잠자는',
        '춤추는', '노래하는', '요리하는', '공부하는', '탐험하는'
    ),
    ' ',
    -- SECOND 카테고리 (성격)
    ELT(FLOOR(1 + RAND() * 10),
        '귀여운', '행복한', '신난', '졸린', '차분한',
        '진지한', '엉뚱한', '똑똑한', '느긋한', '바쁜'
    ),
    ' ',
    -- THIRD 카테고리 (명사)
    ELT(FLOOR(1 + RAND() * 10),
        '고릴라', '원숭이', '고양이', '강아지', '여우',
        '곰', '토끼', '사자', '호랑이', '늑대'
    ),
    id -- account의 id로 중복 완전 방지
)
WHERE nickname IS NULL;

-- 3. account, favorite_folder의 연결 테이블 favorite_folder_account 테이블 추가
CREATE TABLE favorite_folder_account
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    favorite_folder_id BIGINT       NOT NULL,
    account_id         BIGINT       NOT NULL,
    account_role       VARCHAR(255) NOT NULL DEFAULT 'MEMBER',
    CONSTRAINT fk_favorite_folder_account__favorite_folder FOREIGN KEY (favorite_folder_id) REFERENCES favorite_folder (id) ON DELETE CASCADE,
    CONSTRAINT fk_favorite_folder_account__account FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE CASCADE,
    CONSTRAINT uk_favorite_folder_account UNIQUE (favorite_folder_id, account_id)
);

-- 4. favorite_folder에 member_count 컬럼 추가
ALTER TABLE favorite_folder
    ADD COLUMN member_count INT NOT NULL DEFAULT 1;

-- 5. favorite_folder에 is_shared 컬럼 추가
ALTER TABLE favorite_folder
    ADD COLUMN is_shared BOOLEAN NOT NULL DEFAULT FALSE;

-- 6. 기존 favorite_folder의 정보를 기반으로 favorite_folder_account 정보 채우기
-- 각 favorite_folder의 소유자를 OWNER 역할로 추가
INSERT INTO favorite_folder_account (favorite_folder_id, account_id, account_role)
SELECT id, account_id, 'OWNER'
FROM favorite_folder;

-- 7. favorite_folder의 account_id FK 제약조건 제거
ALTER TABLE favorite_folder
    DROP FOREIGN KEY fk_favorite_folder__account;

-- 8. favorite_folder의 (account_id, name) unique 제약조건 제거
ALTER TABLE favorite_folder
DROP INDEX uq_favorite_folder__account_id_name;

-- 9. favorite_folder의 account_id 이 nullable하도록 수정
ALTER TABLE favorite_folder MODIFY COLUMN account_id BIGINT NULL;

-- favorite_folder의 account_id 컬럼은 안정적인 배포 후 contract 과정에서 제거 예정
