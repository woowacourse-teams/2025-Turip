ALTER TABLE fcm_token
    ADD CONSTRAINT uk_fcm_token_token UNIQUE (token);