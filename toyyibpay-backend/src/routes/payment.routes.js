const express = require('express');
const router = express.Router();
const paymentController = require('../controllers/payment.controller');

// Create payment
router.post('/create', (req, res, next) => paymentController.createPayment(req, res, next));

// Get payment status
router.get('/status/:billCode', (req, res, next) => paymentController.getPaymentStatus(req, res, next));

// Get all payments (optional)
router.get('/list', (req, res, next) => paymentController.getAllPayments(req, res, next));

module.exports = router;

