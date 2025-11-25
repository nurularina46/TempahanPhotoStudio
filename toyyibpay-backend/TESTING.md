# 🧪 Testing Guide

Complete testing guide for ToyyibPay backend integration.

## 📋 Prerequisites

- Backend server running (local or deployed)
- Postman or similar API client
- ToyyibPay Sandbox account
- Supabase database set up

## 🔧 Setup

### 1. Start Local Server

```bash
npm install
npm run dev
```

Server should start on `http://localhost:3000`

### 2. Verify Health Check

```bash
curl http://localhost:3000/health
```

Expected response:
```json
{
  "status": "OK",
  "message": "ToyyibPay Backend is running",
  "timestamp": "2024-01-01T00:00:00.000Z"
}
```

## 📮 Postman Collection

### Import Collection

1. Open Postman
2. Click **Import**
3. Import the JSON below or use the file `postman_collection.json`

### Collection JSON

```json
{
  "info": {
    "name": "ToyyibPay Backend API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Health Check",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{base_url}}/health",
          "host": ["{{base_url}}"],
          "path": ["health"]
        }
      }
    },
    {
      "name": "Create Payment",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"name\": \"John Doe\",\n  \"email\": \"john@example.com\",\n  \"phone\": \"0123456789\",\n  \"amount\": 25.00,\n  \"description\": \"Test payment\"\n}"
        },
        "url": {
          "raw": "{{base_url}}/api/payment/create",
          "host": ["{{base_url}}"],
          "path": ["api", "payment", "create"]
        }
      }
    },
    {
      "name": "Get Payment Status",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{base_url}}/api/payment/status/:billCode",
          "host": ["{{base_url}}"],
          "path": ["api", "payment", "status", ":billCode"],
          "variable": [
            {
              "key": "billCode",
              "value": "abc123xyz"
            }
          ]
        }
      }
    },
    {
      "name": "Get All Payments",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{base_url}}/api/payment/list?status=PAID&limit=10",
          "host": ["{{base_url}}"],
          "path": ["api", "payment", "list"],
          "query": [
            {
              "key": "status",
              "value": "PAID"
            },
            {
              "key": "limit",
              "value": "10"
            }
          ]
        }
      }
    },
    {
      "name": "ToyyibPay Callback (Test)",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"billcode\": \"abc123xyz\",\n  \"order_id\": \"order123\",\n  \"status\": \"1\"\n}"
        },
        "url": {
          "raw": "{{base_url}}/api/toyyibpay/callback",
          "host": ["{{base_url}}"],
          "path": ["api", "toyyibpay", "callback"]
        }
      }
    }
  ],
  "variable": [
    {
      "key": "base_url",
      "value": "http://localhost:3000"
    }
  ]
}
```

### Environment Variables

Create a Postman environment with:
- `base_url`: `http://localhost:3000` (local) or your Render URL (production)

## 🧪 Test Cases

### Test 1: Health Check

**Request:**
```bash
GET http://localhost:3000/health
```

**Expected Response:**
```json
{
  "status": "OK",
  "message": "ToyyibPay Backend is running",
  "timestamp": "2024-01-01T00:00:00.000Z"
}
```

**Status Code:** `200 OK`

---

### Test 2: Create Payment (Valid)

**Request:**
```bash
POST http://localhost:3000/api/payment/create
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "0123456789",
  "amount": 25.00,
  "description": "Test payment"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Payment bill created successfully",
  "data": {
    "billCode": "abc123xyz",
    "billUrl": "https://dev.toyyibpay.com/abc123xyz",
    "amount": 25.00,
    "status": "PENDING"
  }
}
```

**Status Code:** `201 Created`

**Next Steps:**
1. Copy `billUrl` from response
2. Open in browser
3. Complete payment in ToyyibPay sandbox

---

### Test 3: Create Payment (Invalid Email)

**Request:**
```bash
POST http://localhost:3000/api/payment/create
Content-Type: application/json

{
  "name": "John Doe",
  "email": "invalid-email",
  "phone": "0123456789",
  "amount": 25.00
}
```

**Expected Response:**
```json
{
  "success": false,
  "message": "Valid email is required"
}
```

**Status Code:** `400 Bad Request`

---

### Test 4: Create Payment (Invalid Amount)

**Request:**
```bash
POST http://localhost:3000/api/payment/create
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "0123456789",
  "amount": -10
}
```

**Expected Response:**
```json
{
  "success": false,
  "message": "Valid amount greater than 0 is required"
}
```

**Status Code:** `400 Bad Request`

---

### Test 5: Get Payment Status

**Request:**
```bash
GET http://localhost:3000/api/payment/status/abc123xyz
```

**Expected Response:**
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "bill_code": "abc123xyz",
    "user_name": "John Doe",
    "user_email": "john@example.com",
    "user_phone": "0123456789",
    "amount": 25.00,
    "status": "PENDING",
    "created_at": "2024-01-01T00:00:00Z",
    "updated_at": "2024-01-01T00:00:00Z"
  }
}
```

**Status Code:** `200 OK`

---

### Test 6: Get Payment Status (Not Found)

**Request:**
```bash
GET http://localhost:3000/api/payment/status/invalid-bill-code
```

**Expected Response:**
```json
{
  "success": false,
  "message": "Payment not found"
}
```

**Status Code:** `404 Not Found`

---

### Test 7: Get All Payments

**Request:**
```bash
GET http://localhost:3000/api/payment/list?status=PAID&limit=10
```

**Expected Response:**
```json
{
  "success": true,
  "count": 2,
  "data": [
    {
      "id": "uuid-1",
      "bill_code": "abc123",
      "status": "PAID",
      ...
    },
    {
      "id": "uuid-2",
      "bill_code": "def456",
      "status": "PAID",
      ...
    }
  ]
}
```

**Status Code:** `200 OK`

---

### Test 8: ToyyibPay Callback (Simulated)

**Request:**
```bash
POST http://localhost:3000/api/toyyibpay/callback
Content-Type: application/json

{
  "billcode": "abc123xyz",
  "order_id": "order123",
  "status": "1"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Callback processed successfully"
}
```

**Status Code:** `200 OK`

**Note:** Status codes:
- `"1"` = PAID
- `"2"` = FAILED
- `"3"` = PENDING

---

## 🔄 Complete Payment Flow Test

### Step 1: Create Payment

```bash
curl -X POST http://localhost:3000/api/payment/create \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "phone": "0123456789",
    "amount": 50.00,
    "description": "Test payment flow"
  }'
```

**Save the `billCode` and `billUrl` from response.**

### Step 2: Check Initial Status

```bash
curl http://localhost:3000/api/payment/status/YOUR_BILL_CODE
```

**Expected:** `status: "PENDING"`

### Step 3: Complete Payment

1. Open `billUrl` in browser
2. Complete payment in ToyyibPay sandbox
3. Use test credentials provided by ToyyibPay

### Step 4: Verify Callback

1. Check server logs for callback request
2. Wait a few seconds for callback processing

### Step 5: Check Updated Status

```bash
curl http://localhost:3000/api/payment/status/YOUR_BILL_CODE
```

**Expected:** `status: "PAID"` (if payment successful)

### Step 6: Verify Database

1. Check Supabase dashboard
2. Verify payment record updated
3. Confirm `status` changed to `PAID`

## 📊 Test Scenarios

### Scenario 1: Successful Payment

1. ✅ Create payment
2. ✅ Get bill URL
3. ✅ Complete payment
4. ✅ Receive callback
5. ✅ Status updated to PAID
6. ✅ Database updated

### Scenario 2: Failed Payment

1. ✅ Create payment
2. ✅ Get bill URL
3. ❌ Payment fails
4. ✅ Receive callback with status "2"
5. ✅ Status updated to FAILED
6. ✅ Database updated

### Scenario 3: Payment Timeout

1. ✅ Create payment
2. ✅ Get bill URL
3. ⏱️ User doesn't complete payment
4. ✅ Status remains PENDING
5. ✅ Can check status later

## 🐛 Error Testing

### Test Invalid Inputs

```bash
# Missing required fields
curl -X POST http://localhost:3000/api/payment/create \
  -H "Content-Type: application/json" \
  -d '{"name": "Test"}'

# Invalid email
curl -X POST http://localhost:3000/api/payment/create \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test",
    "email": "invalid",
    "phone": "0123456789",
    "amount": 25
  }'

# Invalid phone
curl -X POST http://localhost:3000/api/payment/create \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test",
    "email": "test@example.com",
    "phone": "123",
    "amount": 25
  }'

# Negative amount
curl -X POST http://localhost:3000/api/payment/create \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test",
    "email": "test@example.com",
    "phone": "0123456789",
    "amount": -10
  }'
```

## ✅ Testing Checklist

- [ ] Health check endpoint works
- [ ] Create payment with valid data
- [ ] Create payment validation errors
- [ ] Get payment status (existing)
- [ ] Get payment status (not found)
- [ ] Get all payments
- [ ] Filter payments by status
- [ ] Callback endpoint receives data
- [ ] Callback updates database
- [ ] Complete payment flow end-to-end
- [ ] Error handling works
- [ ] Logs are generated correctly

## 📝 Notes

1. **Sandbox Testing**: Use ToyyibPay sandbox for all tests
2. **Test Data**: Use realistic but test data
3. **Callbacks**: May take a few seconds to arrive
4. **Database**: Check Supabase after each operation
5. **Logs**: Monitor server logs for debugging

## 🎯 Expected Results Summary

| Test | Expected Status | Expected Response |
|------|----------------|-------------------|
| Health Check | 200 | OK message |
| Create Payment (Valid) | 201 | Bill URL returned |
| Create Payment (Invalid) | 400 | Error message |
| Get Status (Found) | 200 | Payment data |
| Get Status (Not Found) | 404 | Not found message |
| Callback | 200 | Success message |

## 🚀 Production Testing

Before going live:

1. Test with production ToyyibPay credentials
2. Verify callback URL is publicly accessible
3. Test with real payment amounts
4. Monitor logs for errors
5. Verify database updates
6. Test error scenarios
7. Load test if needed

