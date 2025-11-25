const express = require('express');
const router = express.Router();
const toyyibpayController = require('../controllers/toyyibpay.controller');

// ToyyibPay callback endpoint
router.post('/callback', (req, res, next) => toyyibpayController.handleCallback(req, res, next));

module.exports = router;

