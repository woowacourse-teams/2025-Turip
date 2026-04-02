-- 테스트 환경용 FTS 인덱스 생성
ALTER TABLE content ADD FULLTEXT INDEX idx_title (title) WITH PARSER ngram;
ALTER TABLE creator ADD FULLTEXT INDEX idx_channel_name (channel_name) WITH PARSER ngram;
ALTER TABLE place ADD FULLTEXT INDEX idx_name (name) WITH PARSER ngram;