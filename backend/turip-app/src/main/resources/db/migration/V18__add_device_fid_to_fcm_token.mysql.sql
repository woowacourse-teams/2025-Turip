DELETE FROM fcm_token;

ALTER TABLE fcm_token
    ADD COLUMN device_fid VARCHAR(255) NOT NULL AFTER account_id,
    ADD CONSTRAINT uk_fcm_token__account_id_device_fid UNIQUE (account_id, device_fid);
