-- OneToOne 관계를 보장하기 위한 unique 제약조건 추가
ALTER TABLE social_member
    ADD UNIQUE (member_id);
ALTER TABLE turip_member
    ADD UNIQUE (member_id);
