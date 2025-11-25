# 🚀 Quick Start Guide - Orders & Payments System

## ⚡ Setup Cepat (5 Minit)

### 1️⃣ Setup Database Supabase

```bash
# 1. Buka https://app.supabase.com
# 2. Pilih project anda
# 3. Klik "SQL Editor"
# 4. Copy SQL dari file ORDERS_PAYMENTS_GUIDE.md
# 5. Run SQL untuk create tables: payments dan orders
```

**Penting:** Run 3 SQL blocks ini dalam order:
1. ✅ CREATE TABLE payments
2. ✅ CREATE TABLE orders  
3. ✅ Enable Row Level Security (RLS)

### 2️⃣ Test Backend Locally

```bash
# Install dependencies (jika belum)
npm install

# Start server
npm start

# Server akan run di http://localhost:3000
```

### 3️⃣ Test API dengan Postman

```bash
# Import Postman collection
1. Buka Postman
2. Import → Upload Files
3. Pilih: postman_collection_orders.json
4. Collections akan appear dengan semua endpoints
```

**Test Flow:**
1. ✅ Health Check → `GET /health`
2. ✅ Create Order → `POST /api/order/create`
3. ✅ Get Order → `GET /api/order/{orderNumber}`
4. ✅ Simulate Payment Callback → `POST /api/toyyibpay/callback`
5. ✅ Get Order lagi → Status sudah berubah ke "Confirmed"

---

## 📱 Integration dengan Android App

### Option A: Guna Order API (Recommended) ✅

**Flow:** Android → Backend → ToyyibPay

```kotlin
// 1. Create order (backend akan auto create payment bill)
val orderRequest = CreateOrderRequest(
    user_name = "Ahmad",
    user_email = "ahmad@example.com",
    user_phone = "0123456789",
    package_name = "Wedding Photography",
    event_date = "2025-12-25",
    event_time = "14:00:00",
    base_amount = 1500.00
)

val response = apiService.createOrder(orderRequest)

// 2. Dapat billUrl dari response
val billUrl = response.data.billUrl  // https://dev.toyyibpay.com/xyz123
val orderNumber = response.data.orderNumber  // ORD-20251125-ABC123

// 3. Buka payment page
val intent = Intent(Intent.ACTION_VIEW, Uri.parse(billUrl))
startActivity(intent)

// 4. Lepas user bayar, ToyyibPay akan callback backend
// 5. Order status auto-update ke "Confirmed"
```

### Option B: Create Payment Sahaja (Backward Compatible)

**Flow:** Android → Backend → ToyyibPay (tanpa order)

```kotlin
// Guna existing payment endpoint
val paymentRequest = CreatePaymentRequest(
    name = "Ahmad",
    email = "ahmad@example.com",
    phone = "0123456789",
    amount = 500.00,
    description = "Payment for photography"
)

val response = apiService.createPayment(paymentRequest)
val billUrl = response.data.billUrl
```

---

## 🔄 Payment Callback (Automatic)

Bila user bayar, ToyyibPay akan **automatic** panggil backend:

```
User Bayar → ToyyibPay → POST /api/toyyibpay/callback → Backend Update Status
```

**Yang akan berlaku:**
- ✅ Payment status → `Paid`
- ✅ Order status → `Confirmed` (jika ada order)
- ✅ Timestamp `paid_at` di-set
- ✅ Timestamp `confirmed_at` di-set (untuk order)

**Anda tak perlu buat apa-apa!** 🎉

---

## 🧪 Test Payment Callback Manually

Untuk testing tanpa perlu bayar betul-betul:

```bash
# Simulate successful payment
curl -X POST http://localhost:3000/api/toyyibpay/callback \
  -H "Content-Type: application/json" \
  -d '{
    "billCode": "YOUR_BILL_CODE_HERE",
    "billpaymentStatus": "1",
    "billpaymentAmount": "1400.00"
  }'

# Check order status - sepatutnya sudah "Confirmed"
curl http://localhost:3000/api/order/YOUR_ORDER_NUMBER
```

---

## 📊 Check Data di Supabase

```bash
# 1. Buka Supabase Dashboard
# 2. Klik "Table Editor"
# 3. Pilih table "orders" atau "payments"
# 4. Lihat data yang baru created
```

Anda patut nampak:
- ✅ Orders dengan status "Pending" → "Confirmed"
- ✅ Payments dengan status "Unpaid" → "Paid"
- ✅ Relationship antara order dan payment (order_id, payment_id)

---

## 🐛 Common Issues

### ❌ Error: "Failed to insert payment"

**Solution:**
```bash
# Check Supabase connection
# 1. .env file ada SUPABASE_URL dan SUPABASE_SERVICE_KEY
# 2. RLS policies enabled betul
# 3. Table structure betul
```

### ❌ Error: "Order created but payment failed"

**Solution:**
```bash
# Check ToyyibPay credentials
# 1. .env file ada TOYYIB_USER_SECRET
# 2. TOYYIB_CATEGORY_CODE betul
# 3. Internet connection OK
```

### ❌ Payment callback tidak sampai

**Solution:**
```bash
# Untuk local testing:
# 1. Use ngrok untuk expose local server
ngrok http 3000

# 2. Set ngrok URL sebagai callback URL di ToyyibPay
# Example: https://abc123.ngrok.io/api/toyyibpay/callback

# 3. Test payment dengan real ToyyibPay sandbox
```

---

## 📚 Full Documentation

Untuk documentation lengkap, baca:
- **ORDERS_PAYMENTS_GUIDE.md** - Complete guide dengan SQL, API docs, examples
- **TESTING.md** - Testing procedures
- **DEPLOYMENT.md** - Deploy ke production

---

## ✅ Checklist Setup

- [ ] Supabase tables created (payments, orders)
- [ ] RLS policies enabled
- [ ] Backend running (`npm start`)
- [ ] Environment variables configured (.env)
- [ ] Test dengan Postman - Create order ✅
- [ ] Test payment callback simulation ✅
- [ ] Check data di Supabase ✅
- [ ] Android app updated untuk guna new API
- [ ] End-to-end test dengan real payment

---

## 🎯 Next Steps

1. **Test Local** - Guna Postman untuk test semua endpoints
2. **Update Android** - Integrate dengan Order API
3. **Deploy** - Deploy backend ke production (Railway/Render/Heroku)
4. **Configure ToyyibPay** - Set callback URL ke production URL
5. **Test Production** - Test dengan real payment

---

## 💡 Tips

### Tip 1: Use Order API untuk Full Tracking
```kotlin
// ✅ GOOD - Full tracking
createOrder() // Creates order + payment, dapat order number

// ❌ OK tapi limited - No order tracking  
createPayment() // Only payment, takde order number
```

### Tip 2: Save Order Number untuk Reference
```kotlin
// Save untuk future reference
sharedPreferences.edit()
    .putString("last_order", orderNumber)
    .putString("last_bill_code", billCode)
    .apply()
```

### Tip 3: Poll Order Status Lepas Payment
```kotlin
// Selepas user balik dari payment page
lifecycleScope.launch {
    delay(3000) // Wait 3 seconds
    val order = apiService.getOrder(orderNumber)
    if (order.status == "Confirmed") {
        showSuccessDialog()
    }
}
```

---

## 📞 Need Help?

1. Check logs: `npm start` (akan show semua errors)
2. Check Supabase logs: Dashboard → Logs
3. Check ToyyibPay sandbox dashboard
4. Read full docs: `ORDERS_PAYMENTS_GUIDE.md`

---

**Happy Coding! 🎉**

