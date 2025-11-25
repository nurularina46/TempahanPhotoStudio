-- ============================================
-- ToyyibPay Payment System - Supabase Schema
-- ============================================

-- Create payments table
CREATE TABLE IF NOT EXISTS payments (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  bill_code TEXT NOT NULL UNIQUE,
  user_name TEXT NOT NULL,
  user_email TEXT NOT NULL,
  user_phone TEXT NOT NULL,
  amount NUMERIC(10, 2) NOT NULL,
  status TEXT NOT NULL DEFAULT 'PENDING',
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create index on bill_code for faster lookups
CREATE INDEX IF NOT EXISTS idx_payments_bill_code ON payments(bill_code);

-- Create index on status for filtering
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);

-- Create index on created_at for sorting
CREATE INDEX IF NOT EXISTS idx_payments_created_at ON payments(created_at DESC);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_payments_updated_at
  BEFORE UPDATE ON payments
  FOR EACH ROW
  EXECUTE FUNCTION update_updated_at_column();

-- Enable Row Level Security (RLS)
ALTER TABLE payments ENABLE ROW LEVEL SECURITY;

-- Create policy to allow all operations (adjust based on your security needs)
-- For production, you should create more restrictive policies
CREATE POLICY "Allow all operations for authenticated users"
  ON payments
  FOR ALL
  USING (true)
  WITH CHECK (true);

-- Optional: Create a view for payment statistics
CREATE OR REPLACE VIEW payment_stats AS
SELECT 
  status,
  COUNT(*) as count,
  SUM(amount) as total_amount
FROM payments
GROUP BY status;

-- Comments for documentation
COMMENT ON TABLE payments IS 'Stores payment transactions from ToyyibPay';
COMMENT ON COLUMN payments.bill_code IS 'Unique bill code from ToyyibPay';
COMMENT ON COLUMN payments.status IS 'Payment status: PENDING, PAID, FAILED';
COMMENT ON COLUMN payments.amount IS 'Payment amount in RM';

