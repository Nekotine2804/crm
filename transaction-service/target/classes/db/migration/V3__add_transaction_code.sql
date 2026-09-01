-- Thêm cột transactionCode cho idempotency (POS gửi kèm)
ALTER TABLE transactions ADD COLUMN transaction_code VARCHAR(100) NOT NULL DEFAULT gen_random_uuid()::text;

-- Unique constraint: mỗi transactionCode chỉ xử lý 1 lần
ALTER TABLE transactions ADD CONSTRAINT uk_transactions_transaction_code UNIQUE (transaction_code);

-- Index cho việc lookup duplicate
CREATE INDEX idx_transactions_transaction_code ON transactions(transaction_code);