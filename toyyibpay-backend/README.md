# ToyyibPay Sandbox Integration Backend

Complete Node.js backend for ToyyibPay Sandbox payment integration with Supabase database.

## 🚀 Features

- ✅ ToyyibPay Sandbox API integration
- ✅ Supabase database integration
- ✅ Payment creation and status tracking
- ✅ Webhook callback handling
- ✅ Error handling and validation
- ✅ Production-ready structure

## 📋 Prerequisites

- Node.js >= 18.0.0
- npm >= 9.0.0
- Supabase account
- ToyyibPay Sandbox account

## 🛠️ Installation

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd toyyibpay-backend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Set up environment variables**
   ```bash
   cp .env.example .env
   ```
   
   Edit `.env` and fill in your credentials:
   ```env
   PORT=3000
   NODE_ENV=development
   
   TOYYIBPAY_SECRET_KEY=your_secret_key_here
   TOYYIBPAY_CATEGORY_CODE=your_category_code_here
   TOYYIBPAY_BASE_URL=https://dev.toyyibpay.com/index.php/api/
   
   RETURN_URL=http://localhost:3000/payment-success
   CALLBACK_URL=http://localhost:3000/api/toyyibpay/callback
   
   SUPABASE_URL=https://your-project.supabase.co
   SUPABASE_KEY=your_supabase_anon_key
   SUPABASE_SERVICE_KEY=your_supabase_service_key
   ```

4. **Set up Supabase database**
   - Go to your Supabase project
   - Navigate to SQL Editor
   - Run the SQL script from `supabase/schema.sql`

5. **Start the server**
   ```bash
   # Development mode (with auto-reload)
   npm run dev
   
   # Production mode
   npm start
   ```

## 📁 Project Structure

```
toyyibpay-backend/
├── src/
│   ├── config/
│   │   ├── supabase.config.js    # Supabase configuration
│   │   └── toyyibpay.config.js    # ToyyibPay configuration
│   ├── controllers/
│   │   ├── payment.controller.js  # Payment endpoints
│   │   └── toyyibpay.controller.js # Callback handler
│   ├── services/
│   │   ├── payment.service.js     # Payment business logic
│   │   ├── supabase.service.js    # Supabase operations
│   │   └── toyyibpay.service.js   # ToyyibPay API calls
│   ├── routes/
│   │   ├── payment.routes.js      # Payment routes
│   │   └── toyyibpay.routes.js    # Callback routes
│   └── utils/
│       ├── errors.js              # Custom error classes
│       └── validators.js          # Input validation
├── supabase/
│   └── schema.sql                 # Database schema
├── server.js                      # Main server file
├── package.json
├── .env.example
└── README.md
```

## 🔌 API Endpoints

### 1. Create Payment
**POST** `/api/payment/create`

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "0123456789",
  "amount": 25.00,
  "description": "Payment for services"
}
```

**Response:**
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

### 2. Get Payment Status
**GET** `/api/payment/status/:billCode`

**Response:**
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
    "status": "PAID",
    "created_at": "2024-01-01T00:00:00Z",
    "updated_at": "2024-01-01T00:00:00Z"
  }
}
```

### 3. Get All Payments
**GET** `/api/payment/list?status=PAID&limit=10`

**Query Parameters:**
- `status` (optional): Filter by status (PENDING, PAID, FAILED)
- `limit` (optional): Limit number of results

### 4. Health Check
**GET** `/health`

**Response:**
```json
{
  "status": "OK",
  "message": "ToyyibPay Backend is running",
  "timestamp": "2024-01-01T00:00:00.000Z"
}
```

## 🔄 Payment Flow

1. **Client creates payment** → `POST /api/payment/create`
2. **Backend creates bill** → Calls ToyyibPay API
3. **Backend saves to DB** → Stores in Supabase
4. **Client redirects user** → To `billUrl` from response
5. **User pays** → On ToyyibPay page
6. **ToyyibPay sends callback** → `POST /api/toyyibpay/callback`
7. **Backend updates status** → Updates Supabase
8. **Client checks status** → `GET /api/payment/status/:billCode`

## 🧪 Testing

See `TESTING.md` for detailed testing instructions and Postman collection.

## 🚢 Deployment

See `DEPLOYMENT.md` for detailed deployment instructions to Render.

## 📝 Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `PORT` | Server port | No (default: 3000) |
| `NODE_ENV` | Environment (development/production) | No |
| `TOYYIBPAY_SECRET_KEY` | ToyyibPay secret key | Yes |
| `TOYYIBPAY_CATEGORY_CODE` | ToyyibPay category code | Yes |
| `TOYYIBPAY_BASE_URL` | ToyyibPay API base URL | No (default: sandbox) |
| `RETURN_URL` | URL to redirect after payment | Yes |
| `CALLBACK_URL` | URL for ToyyibPay callbacks | Yes |
| `SUPABASE_URL` | Supabase project URL | Yes |
| `SUPABASE_KEY` | Supabase anon key | Yes |
| `SUPABASE_SERVICE_KEY` | Supabase service key | Yes |

## 🔒 Security Notes

- Never commit `.env` file
- Use environment variables for all secrets
- Enable RLS policies in Supabase for production
- Validate all input data
- Use HTTPS in production

## 📚 Additional Resources

- [ToyyibPay Documentation](https://toyyibpay.com)
- [Supabase Documentation](https://supabase.com/docs)
- [Render Documentation](https://render.com/docs)

## 🐛 Troubleshooting

### Common Issues

1. **"Supabase configuration is missing"**
   - Check your `.env` file has all Supabase variables

2. **"ToyyibPay configuration is missing"**
   - Verify `TOYYIBPAY_SECRET_KEY` and `TOYYIBPAY_CATEGORY_CODE` are set

3. **Callback not working**
   - Ensure `CALLBACK_URL` is publicly accessible
   - Check Render logs for callback requests
   - Verify URL is correct in ToyyibPay dashboard

4. **Database connection errors**
   - Verify Supabase URL and keys
   - Check Supabase project is active
   - Ensure table exists (run schema.sql)

## 📄 License

ISC

## 👤 Author

Your Name

