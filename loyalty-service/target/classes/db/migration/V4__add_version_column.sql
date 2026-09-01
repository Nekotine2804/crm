-- Thêm cột version cho optimistic locking
ALTER TABLE loyalty_accounts ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;
ALTER TABLE point_transactions ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;
