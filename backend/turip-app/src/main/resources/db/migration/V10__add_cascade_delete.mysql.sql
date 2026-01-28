-- 1. 기존 FK 제약조건 삭제
ALTER TABLE guest
DROP FOREIGN KEY fk_guest__account;

ALTER TABLE member
DROP FOREIGN KEY fk_member__account;

-- 2. CASCADE 옵션 포함해서 재생성
ALTER TABLE guest
    ADD CONSTRAINT fk_guest__account
        FOREIGN KEY (account_id)
            REFERENCES account(id)
            ON DELETE CASCADE;

ALTER TABLE member
    ADD CONSTRAINT fk_member__account
        FOREIGN KEY (account_id)
            REFERENCES account(id)
            ON DELETE CASCADE;
