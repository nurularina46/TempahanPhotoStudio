# 📚 Panduan Orders & Payments System

## 🎯 Overview

Backend ini sekarang support 2 table utama:
- **`payments`** - Simpan maklumat pembayaran ToyyibPay
- **`orders`** - Simpan maklumat booking/order pelanggan

Kedua-dua table ini **terintegrasi** - bila payment status berubah, order status akan auto-update.

---

## 🗄️ Setup Database (Supabase)

### Langkah 1: Buka Supabase SQL Editor

1. Pergi ke https://app.supabase.com
2. Pilih project anda
3. Klik **SQL Editor** di sidebar kiri

### Langkah 2: Run SQL Untuk Create Tables

Copy dan paste SQL ini **satu persatu**:

#### **Table: payments**

```sql
-- Table untuk simpan maklumat payment
CREATE TABLE payments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  
  -- Bill Information
  bill_code VARCHAR(50) UNIQUE NOT NULL,
  bill_url TEXT,
  bill_name VARCHAR(255),
  bill_description TEXT,
  
  -- User Information
  user_name VARCHAR(255),
  user_email VARCHAR(255),
  user_phone VARCHAR(20),
  user_id UUID,
  
  -- Booking/Order Information
  booking_id UUID,
  order_id UUID,
  external_reference_no VARCHAR(100),
  
  -- Package Information
  package_id UUID,
  package_name VARCHAR(255),
  sub_package_id UUID,
  sub_package_name VARCHAR(255),
  
  -- Payment Details
  amount DECIMAL(10, 2) NOT NULL,
  payment_method VARCHAR(50) DEFAULT 'ToyyibPay',
  
  -- Status Tracking
  status VARCHAR(50) DEFAULT 'Unpaid',
  payment_status VARCHAR(50) DEFAULT 'Unpaid',
  toyyibpay_status INTEGER,
  
  -- Transaction Details
  toyyibpay_transaction_id VARCHAR(100),
  toyyibpay_order_id VARCHAR(100),
  
  -- Booking Details
  booking_date DATE,
  event_date DATE,
  event_time TIME,
  notes TEXT,
  
  -- Timestamps
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  callback_received_at TIMESTAMP WITH TIME ZONE,
  paid_at TIMESTAMP WITH TIME ZONE
);

-- Create indexes
CREATE INDEX idx_payments_bill_code ON payments(bill_code);
CREATE INDEX idx_payments_user_email ON payments(user_email);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_payments_created_at ON payments(created_at DESC);

-- Auto-update updated_at timestamp
CREATE OR REPLACE FUNCTION update_payments_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_payments_updated_at
  BEFORE UPDATE ON payments
  FOR EACH ROW
  EXECUTE FUNCTION update_payments_updated_at();

-- Auto-set paid_at bila status jadi Paid
CREATE OR REPLACE FUNCTION set_payments_paid_at()
RETURNS TRIGGER AS $$
BEGIN
  IF NEW.status = 'Paid' AND (OLD.status IS NULL OR OLD.status != 'Paid') THEN
    NEW.paid_at = NOW();
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_set_payments_paid_at
  BEFORE UPDATE ON payments
  FOR EACH ROW
  EXECUTE FUNCTION set_payments_paid_at();
```

#### **Table: orders**

```sql
-- Table untuk simpan maklumat order/booking
CREATE TABLE orders (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  
  -- Order Information
  order_number VARCHAR(50) UNIQUE NOT NULL,
  order_type VARCHAR(50) DEFAULT 'booking',
  
  -- User Information
  user_id UUID,
  user_name VARCHAR(255) NOT NULL,
  user_email VARCHAR(255) NOT NULL,
  user_phone VARCHAR(20),
  user_address TEXT,
  
  -- Package Information
  package_id UUID,
  package_name VARCHAR(255),
  sub_package_id UUID,
  sub_package_name VARCHAR(255),
  
  -- Event/Booking Details
  event_date DATE,
  event_time TIME,
  event_location TEXT,
  event_type VARCHAR(100),
  
  -- Pricing
  base_amount DECIMAL(10, 2) NOT NULL,
  discount_amount DECIMAL(10, 2) DEFAULT 0,
  tax_amount DECIMAL(10, 2) DEFAULT 0,
  total_amount DECIMAL(10, 2) NOT NULL,
  
  -- Payment Information
  payment_id UUID REFERENCES payments(id),
  payment_status VARCHAR(50) DEFAULT 'Unpaid',
  payment_method VARCHAR(50),
  
  -- Order Status
  status VARCHAR(50) DEFAULT 'Pending',
  
  -- Additional Information
  notes TEXT,
  special_requests TEXT,
  terms_accepted BOOLEAN DEFAULT FALSE,
  
  -- Timestamps
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  confirmed_at TIMESTAMP WITH TIME ZONE,
  completed_at TIMESTAMP WITH TIME ZONE,
  cancelled_at TIMESTAMP WITH TIME ZONE,
  
  -- Metadata
  metadata JSONB
);

-- Create indexes
CREATE INDEX idx_orders_order_number ON orders(order_number);
CREATE INDEX idx_orders_user_email ON orders(user_email);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_payment_status ON orders(payment_status);
CREATE INDEX idx_orders_event_date ON orders(event_date);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);
CREATE INDEX idx_orders_payment_id ON orders(payment_id);

-- Auto-update updated_at timestamp
CREATE OR REPLACE FUNCTION update_orders_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_orders_updated_at
  BEFORE UPDATE ON orders
  FOR EACH ROW
  EXECUTE FUNCTION update_orders_updated_at();
```

#### **Enable Row Level Security (RLS) - PENTING!**

```sql
-- Enable RLS
ALTER TABLE payments ENABLE ROW LEVEL SECURITY;
ALTER TABLE orders ENABLE ROW LEVEL SECURITY;

-- Policy untuk service role (backend) - full access
CREATE POLICY "Service role can do everything on payments"
  ON payments
  FOR ALL
  TO service_role
  USING (true)
  WITH CHECK (true);

CREATE POLICY "Service role can do everything on orders"
  ON orders
  FOR ALL
  TO service_role
  USING (true)
  WITH CHECK (true);

-- Policy untuk authenticated users - view their own data
CREATE POLICY "Users can view their own payments"
  ON payments
  FOR SELECT
  TO authenticated
  USING (user_email = auth.email());

CREATE POLICY "Users can view their own orders"
  ON orders
  FOR SELECT
  TO authenticated
  USING (user_email = auth.email());
```

### Langkah 3: Verify Tables

Pergi ke **Table Editor** dan pastikan:
- ✅ Table `payments` wujud
- ✅ Table `orders` wujud
- ✅ Semua columns betul
- ✅ Indexes & triggers tercipta

---

## 🚀 API Endpoints

Backend anda sekarang ada endpoint-endpoint baru:

### **Orders API**

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/order/create` | Create order + payment |
| GET | `/api/order/:orderNumber` | Get order by order number |
| GET | `/api/order/list` | Get all orders (with filters) |
| GET | `/api/order/user/:email` | Get orders by user email |
| PATCH | `/api/order/:orderNumber/status` | Update order status |
| POST | `/api/order/:orderNumber/cancel` | Cancel order |

### **Payments API** (Existing)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/payment/create` | Create payment bill |
| GET | `/api/payment/status/:billCode` | Get payment status |
| GET | `/api/payment/list` | Get all payments |

### **ToyyibPay Callback** (Existing)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/toyyibpay/callback` | ToyyibPay payment callback |

---

## 📱 Contoh Penggunaan dari Android App

### **Scenario 1: User Buat Booking (Recommended Flow)**

Guna endpoint `/api/order/create` - sistem akan auto create order + payment sekaligus.

#### **Request dari Android:**

```kotlin
// Kotlin/Android Example
data class CreateOrderRequest(
    // User info
    val user_name: String,
    val user_email: String,
    val user_phone: String,
    val user_address: String? = null,
    
    // Package info
    val package_id: Int?,
    val package_name: String,
    val sub_package_id: Int?,
    val sub_package_name: String?,
    
    // Event details
    val event_date: String,  // Format: "2025-12-25"
    val event_time: String,  // Format: "14:00:00"
    val event_location: String?,
    val event_type: String?,
    
    // Pricing
    val base_amount: Double,
    val discount_amount: Double? = 0.0,
    val tax_amount: Double? = 0.0,
    
    // Other
    val notes: String?,
    val special_requests: String?,
    val terms_accepted: Boolean = false
)

// API Call
val request = CreateOrderRequest(
    user_name = "Ahmad bin Ali",
    user_email = "ahmad@example.com",
    user_phone = "0123456789",
    user_address = "123, Jalan ABC, Kuala Lumpur",
    
    package_name = "Wedding Photography Premium",
    sub_package_name = "Full Day Coverage",
    
    event_date = "2025-12-25",
    event_time = "14:00:00",
    event_location = "Dewan Serbaguna, Cyberjaya",
    event_type = "Wedding",
    
    base_amount = 1500.00,
    discount_amount = 100.00,
    
    notes = "Please bring extra lenses",
    terms_accepted = true
)

val response = api.createOrder(request)
```

#### **HTTP Request:**

```http
POST /api/order/create HTTP/1.1
Host: your-backend-url.com
Content-Type: application/json

{
  "user_name": "Ahmad bin Ali",
  "user_email": "ahmad@example.com",
  "user_phone": "0123456789",
  "user_address": "123, Jalan ABC, Kuala Lumpur",
  
  "package_name": "Wedding Photography Premium",
  "sub_package_name": "Full Day Coverage",
  
  "event_date": "2025-12-25",
  "event_time": "14:00:00",
  "event_location": "Dewan Serbaguna, Cyberjaya",
  "event_type": "Wedding",
  
  "base_amount": 1500.00,
  "discount_amount": 100.00,
  "tax_amount": 0,
  
  "notes": "Please bring extra lenses",
  "special_requests": "Need drone shots",
  "terms_accepted": true
}
```

#### **Response:**

```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "orderNumber": "ORD-20251125-ABC123",
    "orderId": "550e8400-e29b-41d4-a716-446655440000",
    "billCode": "xyz789abc",
    "billUrl": "https://dev.toyyibpay.com/xyz789abc",
    "totalAmount": 1400.00,
    "status": "Pending",
    "paymentStatus": "Unpaid"
  }
}
```

#### **Android - Buka Payment URL:**

```kotlin
// Terima response
if (response.success) {
    val billUrl = response.data.billUrl
    val orderNumber = response.data.orderNumber
    
    // Save order number untuk reference
    sharedPreferences.edit()
        .putString("current_order", orderNumber)
        .apply()
    
    // Buka ToyyibPay payment page
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(billUrl))
    startActivity(intent)
}
```

---

### **Scenario 2: Check Order Status**

#### **Request:**

```http
GET /api/order/ORD-20251125-ABC123 HTTP/1.1
Host: your-backend-url.com
```

#### **Response:**

```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "order_number": "ORD-20251125-ABC123",
    "order_type": "booking",
    "user_name": "Ahmad bin Ali",
    "user_email": "ahmad@example.com",
    "user_phone": "0123456789",
    "package_name": "Wedding Photography Premium",
    "event_date": "2025-12-25",
    "event_time": "14:00:00",
    "total_amount": 1400.00,
    "status": "Confirmed",
    "payment_status": "Paid",
    "created_at": "2025-11-25T12:00:00Z",
    "confirmed_at": "2025-11-25T12:15:00Z",
    "payment": {
      "bill_code": "xyz789abc",
      "bill_url": "https://dev.toyyibpay.com/xyz789abc",
      "amount": 1400.00,
      "status": "Paid",
      "paid_at": "2025-11-25T12:15:00Z"
    }
  }
}
```

---

### **Scenario 3: Get User's All Orders**

#### **Request:**

```http
GET /api/order/user/ahmad@example.com HTTP/1.1
Host: your-backend-url.com
```

#### **Response:**

```json
{
  "success": true,
  "count": 2,
  "data": [
    {
      "order_number": "ORD-20251125-ABC123",
      "package_name": "Wedding Photography Premium",
      "event_date": "2025-12-25",
      "total_amount": 1400.00,
      "status": "Confirmed",
      "payment_status": "Paid",
      "created_at": "2025-11-25T12:00:00Z"
    },
    {
      "order_number": "ORD-20251120-XYZ456",
      "package_name": "Birthday Photography",
      "event_date": "2025-12-01",
      "total_amount": 500.00,
      "status": "Completed",
      "payment_status": "Paid",
      "created_at": "2025-11-20T10:00:00Z"
    }
  ]
}
```

---

## 🔄 Payment Callback Flow

Bila user bayar di ToyyibPay:

1. **User bayar** di ToyyibPay payment page
2. **ToyyibPay hantar callback** ke `/api/toyyibpay/callback`
3. **Backend auto-update**:
   - ✅ Payment status → `Paid`
   - ✅ Order status → `Confirmed`
   - ✅ Set `paid_at` timestamp
   - ✅ Set `confirmed_at` timestamp
4. **User dapat redirect** ke return URL

Anda **tak perlu buat apa-apa** - semuanya automatic!

---

## 🎨 Order Status Flow

```
Pending → Confirmed → In Progress → Completed
   ↓
Cancelled (boleh cancel bila-bila masa)
```

**Status Explanation:**

| Status | Description | Bila Digunakan |
|--------|-------------|----------------|
| `Pending` | Order baru dibuat, tunggu payment | Auto set bila create order |
| `Confirmed` | Payment successful | Auto set bila payment callback status = Paid |
| `In Progress` | Photographer sedang kerja | Manual update |
| `Completed` | Kerja siap, photo delivered | Manual update |
| `Cancelled` | Order dibatalkan | Manual atau auto |
| `PaymentFailed` | Payment gagal | Auto set bila payment callback status = Failed |
| `PaymentError` | Error create payment | Auto set bila ToyyibPay API error |

---

## 🔧 Admin Operations

### **Get All Orders (with filters)**

```http
GET /api/order/list?status=Confirmed&limit=50 HTTP/1.1
```

### **Update Order Status Manually**

```http
PATCH /api/order/ORD-20251125-ABC123/status HTTP/1.1
Content-Type: application/json

{
  "status": "In Progress",
  "notes": "Photographer on the way"
}
```

### **Cancel Order**

```http
POST /api/order/ORD-20251125-ABC123/cancel HTTP/1.1
Content-Type: application/json

{
  "reason": "Customer requested cancellation"
}
```

---

## 🐛 Troubleshooting

### Error: "Order created but payment failed"

**Sebab:** ToyyibPay API error (DNS issue, network issue, invalid credentials)

**Solution:**
1. Check `.env` file - pastikan `TOYYIB_USER_SECRET` betul
2. Check internet connection
3. Order tetap tercipta - boleh cuba create payment manually
4. Check order status: `GET /api/order/{orderNumber}`

### Payment callback tidak sampai

**Sebab:** ToyyibPay tak boleh reach backend URL

**Solution:**
1. Pastikan backend deployed di public URL (bukan localhost)
2. Set callback URL di ToyyibPay dashboard
3. Test callback manually:
   ```bash
   curl -X POST http://your-backend/api/toyyibpay/callback \
     -H "Content-Type: application/json" \
     -d '{"billCode":"xyz123","billpaymentStatus":"1"}'
   ```

---

## 📊 Database Relationship

```
┌─────────────┐         ┌──────────────┐
│   orders    │         │   payments   │
│─────────────│         │──────────────│
│ id (PK)     │◄────┐   │ id (PK)      │
│ order_number│     │   │ bill_code    │
│ user_email  │     └───│ order_id (FK)│
│ package_name│         │ amount       │
│ total_amount│         │ status       │
│ status      │         │ paid_at      │
│ payment_id  │─────────►│              │
└─────────────┘         └──────────────┘
```

**Key Points:**
- Order → Payment: `payment_id` (optional, can be NULL)
- Payment → Order: `order_id` (optional, for linking)
- Bila payment status update, order status auto-update
- One order can have one payment
- Payment boleh exist without order (backward compatibility)

---

## ✅ Next Steps

1. ✅ Tables created di Supabase
2. ✅ Backend code updated
3. ⬜ Test API endpoints (guna Postman)
4. ⬜ Update Android app untuk guna `/api/order/create`
5. ⬜ Test payment flow end-to-end
6. ⬜ Deploy backend ke production

---

## 📞 Support

Jika ada masalah:
1. Check server logs: `npm start` atau `pm2 logs`
2. Check Supabase logs: Dashboard → Logs
3. Test dengan Postman dulu sebelum test dengan Android app
4. Pastikan `.env` file configured betul

---

**Happy Coding! 🚀**

