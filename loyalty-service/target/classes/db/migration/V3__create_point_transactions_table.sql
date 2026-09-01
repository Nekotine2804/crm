-- Point Transaction table: ghi nhận lịch sử tích/đổi điểm
CREATE TABLE point_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    loyalty_account_id UUID NOT NULL REFERENCES loyalty_accounts(id),
    customer_id UUID NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('EARN', 'REDEEM', 'ADJUST', 'EXPIRE', 'REFUND')),
    points INT NOT NULL,                           -- Dương = cộng điểm, Âm = trừ điểm
    balance_after INT NOT NULL,                    -- Số dư sau giao dịch
    reference_id VARCHAR(100),                    -- ID tham chiếu (transaction ID, redemption ID)
    description VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    
    -- Indexes
    CONSTRAINT fk_point_tx_loyalty_account 
        FOREIGN KEY (loyalty_account_id) REFERENCES loyalty_accounts(id)
);

-- Index for fast lookup by customer
CREATE INDEX idx_point_tx_customer_id ON point_transactions(customer_id);

-- Index for fast lookup by loyalty account
CREATE INDEX idx_point_tx_loyalty_account_id ON point_transactions(loyalty_account_id);

-- Index for filtering by type
CREATE INDEX idx_point_tx_type ON point_transactions(type);

-- Index for sorting by created_at
CREATE INDEX idx_point_tx_created_at ON point_transactions(created_at);

-- Unique constraint for idempotency (1 transaction chỉ tích điểm 1 lần)
CREATE UNIQUE INDEX idx_point_tx_unique_earn 
    ON point_transactions(reference_id) 
    WHERE type = 'EARN';

COMMENT ON TABLE point_transactions IS 'Bảng ghi nhận lịch sử tích/đổi điểm loyalty';
COMMENT ON COLUMN point_transactions.points IS 'Số điểm: dương = cộng, âm = trừ';
COMMENT ON COLUMN point_transactions.reference_id IS 'ID tham chiếu: transaction ID cho EARN, redemption ID cho REDEEM';
