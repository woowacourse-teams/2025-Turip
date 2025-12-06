-- 기존 Fulltext Index 제거
ALTER TABLE content DROP INDEX idx_title;
ALTER TABLE creator DROP INDEX idx_channel_name;
ALTER TABLE place DROP INDEX idx_name;

-- ngram_token_size=1 설정 후, 새 인덱스 생성
ALTER TABLE content
    ADD FULLTEXT INDEX idx_title (title) WITH PARSER ngram;

ALTER TABLE creator
    ADD FULLTEXT INDEX idx_channel_name (channel_name) WITH PARSER ngram;

ALTER TABLE place
    ADD FULLTEXT INDEX idx_name (name) WITH PARSER ngram;
