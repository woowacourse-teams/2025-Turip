ALTER TABLE member ADD COLUMN is_migration_decided BOOLEAN NOT NULL DEFAULT false;

UPDATE member SET is_migration_decided = true;