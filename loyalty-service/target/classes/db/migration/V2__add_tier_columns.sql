-- Thêm cột cho rolling window tier evaluation
ALTER TABLE loyalty_accounts
    ADD COLUMN pending_tier VARCHAR(20),
    ADD COLUMN last_tier_evaluation TIMESTAMP;

-- Tạo bảng theo dõi spending history (cho rolling window calculation)
CREATE TABLE spending_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES loyalty_accounts(customer_id),
    transaction_id UUID NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    spent_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_spending_history_customer_id ON spending_history(customer_id);
CREATE INDEX idx_spending_history_spent_at ON spending_history(spent_at);
CREATE INDEX idx_spending_history_customer_spent ON spending_history(customer_id, spent_at);