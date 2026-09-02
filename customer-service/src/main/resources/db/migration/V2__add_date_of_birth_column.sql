-- Thêm cột date_of_birth để hỗ trợ gửi thông báo chúc mừng sinh nhật
ALTER TABLE customers
ADD COLUMN date_of_birth DATE;

COMMENT ON COLUMN customers.date_of_birth IS 'Ngày sinh của khách hàng - dùng để gửi thông báo chúc mừng sinh nhật';