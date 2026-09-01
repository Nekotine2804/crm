-- Thêm các cột cho refund/cancel
ALTER TABLE transactions
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE,
ADD COLUMN IF NOT EXISTS cancelled_at TIMESTAMP WITH TIME ZONE,
ADD COLUMN IF NOT EXISTS refund_reason VARCHAR(255);

-- Thêm index cho status
CREATE INDEX IF NOT EXISTS idx_transactions_status ON transactions(status);

-- Thêm index cho customer_id
CREATE INDEX IF NOT EXISTS idx_transactions_customer_id ON transactions(customer_id);

COMMENT ON TABLE transactions IS 'Bảng giao dịch POS';
COMMENT ON COLUMN transactions.refund_reason IS 'Lý do refund (nếu có)';
