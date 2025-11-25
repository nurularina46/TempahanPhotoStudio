require('dotenv').config();
const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');

const paymentRoutes = require('./src/routes/payment.routes');
const toyyibpayRoutes = require('./src/routes/toyyibpay.routes');
const orderRoutes = require('./src/routes/order.routes');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(helmet());
app.use(cors());
app.use(morgan('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Health check
app.get('/health', (req, res) => {
  res.status(200).json({
    status: 'OK',
    message: 'ToyyibPay Backend is running',
    timestamp: new Date().toISOString()
  });
});

// Routes
app.use('/api/payment', paymentRoutes);
app.use('/api/toyyibpay', toyyibpayRoutes);
app.use('/api/order', orderRoutes);

// Payment success page (for testing)
app.get('/payment-success', (req, res) => {
  res.send(`
    <html>
      <head>
        <title>Payment Success</title>
        <style>
          body {
            font-family: Arial, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          }
          .container {
            background: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
            text-align: center;
          }
          h1 { color: #4CAF50; }
        </style>
      </head>
      <body>
        <div class="container">
          <h1>✅ Payment Successful!</h1>
          <p>Thank you for your payment.</p>
          <p>Your transaction is being processed.</p>
        </div>
      </body>
    </html>
  `);
});

// Error handling middleware
app.use((err, req, res, next) => {
  console.error('Error:', err);
  res.status(err.status || 500).json({
    success: false,
    message: err.message || 'Internal Server Error',
    ...(process.env.NODE_ENV === 'development' && { stack: err.stack })
  });
});

// 404 handler
app.use((req, res) => {
  res.status(404).json({
    success: false,
    message: 'Route not found'
  });
});

// Start server
app.listen(PORT, () => {
  console.log(`🚀 Server running on port ${PORT}`);
  console.log(`📝 Environment: ${process.env.NODE_ENV || 'development'}`);
  console.log(`🔗 Health check: http://localhost:${PORT}/health`);
});

module.exports = app;

