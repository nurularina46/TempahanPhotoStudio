const express = require('express');
const router = express.Router();
const orderController = require('../controllers/order.controller');

/**
 * @route   POST /api/order/create
 * @desc    Create a new order with payment
 * @access  Public
 * @body    {
 *   user_name, user_email, user_phone, user_address,
 *   package_id, package_name, sub_package_id, sub_package_name,
 *   event_date, event_time, event_location, event_type,
 *   base_amount, discount_amount, tax_amount, total_amount,
 *   order_type, notes, special_requests, terms_accepted,
 *   bill_code, bill_url (optional - if bill already created)
 * }
 */
router.post('/create', orderController.createOrder.bind(orderController));

/**
 * @route   GET /api/order/list
 * @desc    Get all orders (with optional filters)
 * @access  Public (should be protected in production)
 * @query   status, payment_status, user_email, limit
 */
router.get('/list', orderController.getAllOrders.bind(orderController));

/**
 * @route   GET /api/order/:orderNumber
 * @desc    Get order by order number
 * @access  Public
 */
router.get('/:orderNumber', orderController.getOrder.bind(orderController));

/**
 * @route   GET /api/order/user/:email
 * @desc    Get orders by user email
 * @access  Public
 */
router.get('/user/:email', orderController.getUserOrders.bind(orderController));

/**
 * @route   PATCH /api/order/:orderNumber/status
 * @desc    Update order status
 * @access  Public (should be protected in production)
 * @body    { status: String, ...additionalData }
 */
router.patch('/:orderNumber/status', orderController.updateOrderStatus.bind(orderController));

/**
 * @route   POST /api/order/:orderNumber/cancel
 * @desc    Cancel an order
 * @access  Public
 * @body    { reason: String }
 */
router.post('/:orderNumber/cancel', orderController.cancelOrder.bind(orderController));

module.exports = router;

