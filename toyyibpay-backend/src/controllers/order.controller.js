const orderService = require('../services/order.service');
const { ValidationError, NotFoundError } = require('../utils/errors');

class OrderController {
  /**
   * Create a new order with payment
   * POST /api/order/create
   */
  async createOrder(req, res, next) {
    try {
      const {
        // User info
        user_id,
        user_name,
        user_email,
        user_phone,
        user_address,

        // Package info
        package_id,
        package_name,
        sub_package_id,
        sub_package_name,

        // Event details
        event_date,
        event_time,
        event_location,
        event_type,

        // Pricing
        base_amount,
        discount_amount,
        tax_amount,
        total_amount,

        // Order details
        order_type,
        notes,
        special_requests,
        terms_accepted,

        // Payment info (if bill already created in Android)
        bill_code,
        bill_url,

        metadata
      } = req.body;

      // Validate required fields
      if (!user_name || !user_email || !base_amount) {
        throw new ValidationError('user_name, user_email, and base_amount are required');
      }

      // Create order
      const result = await orderService.createOrder({
        user_id,
        user_name: user_name.trim(),
        user_email: user_email.trim().toLowerCase(),
        user_phone: user_phone?.trim(),
        user_address,
        package_id,
        package_name,
        sub_package_id,
        sub_package_name,
        event_date,
        event_time,
        event_location,
        event_type,
        base_amount: parseFloat(base_amount),
        discount_amount: discount_amount ? parseFloat(discount_amount) : 0,
        tax_amount: tax_amount ? parseFloat(tax_amount) : 0,
        total_amount: total_amount ? parseFloat(total_amount) : undefined,
        order_type: order_type || 'booking',
        notes,
        special_requests,
        terms_accepted: terms_accepted || false,
        bill_code,
        bill_url,
        metadata
      });

      res.status(201).json({
        success: true,
        message: 'Order created successfully',
        data: {
          orderNumber: result.orderNumber,
          orderId: result.order.id,
          billCode: result.billCode,
          billUrl: result.billUrl,
          totalAmount: result.totalAmount,
          status: result.order.status,
          paymentStatus: result.order.payment_status
        }
      });
    } catch (error) {
      next(error);
    }
  }

  /**
   * Get order by order number
   * GET /api/order/:orderNumber
   */
  async getOrder(req, res, next) {
    try {
      const { orderNumber } = req.params;

      if (!orderNumber) {
        throw new ValidationError('Order number is required');
      }

      const order = await orderService.getOrder(orderNumber);

      if (!order) {
        throw new NotFoundError('Order not found');
      }

      res.status(200).json({
        success: true,
        data: order
      });
    } catch (error) {
      next(error);
    }
  }

  /**
   * Get all orders with optional filters
   * GET /api/order/list
   */
  async getAllOrders(req, res, next) {
    try {
      const { status, payment_status, user_email, limit } = req.query;

      const orders = await orderService.getAllOrders({
        status,
        payment_status,
        user_email,
        limit: limit ? parseInt(limit) : undefined
      });

      res.status(200).json({
        success: true,
        count: orders.length,
        data: orders
      });
    } catch (error) {
      next(error);
    }
  }

  /**
   * Get orders by user email
   * GET /api/order/user/:email
   */
  async getUserOrders(req, res, next) {
    try {
      const { email } = req.params;

      if (!email) {
        throw new ValidationError('Email is required');
      }

      const orders = await orderService.getUserOrders(email);

      res.status(200).json({
        success: true,
        count: orders.length,
        data: orders
      });
    } catch (error) {
      next(error);
    }
  }

  /**
   * Update order status
   * PATCH /api/order/:orderNumber/status
   */
  async updateOrderStatus(req, res, next) {
    try {
      const { orderNumber } = req.params;
      const { status, ...additionalData } = req.body;

      if (!orderNumber) {
        throw new ValidationError('Order number is required');
      }

      if (!status) {
        throw new ValidationError('Status is required');
      }

      // Validate status
      const validStatuses = ['Pending', 'Confirmed', 'In Progress', 'Completed', 'Cancelled'];
      if (!validStatuses.includes(status)) {
        throw new ValidationError(`Invalid status. Valid statuses: ${validStatuses.join(', ')}`);
      }

      const result = await orderService.updateOrderStatus(
        orderNumber,
        status,
        additionalData
      );

      res.status(200).json({
        success: true,
        message: 'Order status updated successfully',
        data: result
      });
    } catch (error) {
      next(error);
    }
  }

  /**
   * Cancel order
   * POST /api/order/:orderNumber/cancel
   */
  async cancelOrder(req, res, next) {
    try {
      const { orderNumber } = req.params;
      const { reason } = req.body;

      if (!orderNumber) {
        throw new ValidationError('Order number is required');
      }

      const result = await orderService.cancelOrder(orderNumber, reason || '');

      res.status(200).json({
        success: true,
        message: 'Order cancelled successfully',
        data: result
      });
    } catch (error) {
      next(error);
    }
  }
}

module.exports = new OrderController();

