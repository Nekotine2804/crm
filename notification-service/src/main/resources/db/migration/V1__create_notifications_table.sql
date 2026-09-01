-- Notifications table
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL,
    type VARCHAR(30) NOT NULL CHECK (type IN ('POINT_EARNED', 'TIER_UPGRADE', 'REDEEM_SUCCESS', 'REFUND', 'BIRTHDAY', 'PROMOTION')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'SENT', 'FAILED', 'READ')),
    title VARCHAR(100) NOT NULL,
    content VARCHAR(500) NOT NULL,
    channel VARCHAR(20) NOT NULL DEFAULT 'SMS',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    sent_at TIMESTAMP WITH TIME ZONE
);

-- Indexes
CREATE INDEX idx_notification_customer_id ON notifications(customer_id);
CREATE INDEX idx_notification_type ON notifications(type);
CREATE INDEX idx_notification_status ON notifications(status);
CREATE INDEX idx_notification_created_at ON notifications(created_at DESC);

COMMENT ON TABLE notifications IS 'Bảng lưu notification gửi đến khách hàng';
