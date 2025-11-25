const paymentService = require('../services/payment.service');
const { ValidationError, NotFoundError } = require('../utils/errors');

class PaymentController {
  /**
   * Create a new payment
   * POST /api/payment/create
   */
  async createPayment(req, res, next) {
    try {
      const { 
        name, 
        email, 
        phone, 
        amount, 
        description,
        // Additional booking data from Android app
        user_id,
        booking_id,
        package_id,
        package_name,
        sub_package_id,
        sub_package_name,
        booking_date,
        event_date,
        event_time,
        bill_code,
        bill_url
      } = req.body;

      // Validate required input
      const validation = require('../utils/validators').validatePaymentRequest(req.body);
      if (!validation.valid) {
        throw new ValidationError(validation.errors.join(', '));
      }

      // Create payment with all booking data
      const result = await paymentService.createPayment({
        name: name ? name.trim() : '',
        email: email ? email.trim().toLowerCase() : '',
        phone: phone ? phone.trim() : '',
        amount: parseFloat(amount),
        description: description || `Payment for ${package_name || 'booking'}`,
        // Pass all booking data to service
        user_id: user_id ? parseInt(user_id) : undefined,
        booking_id: booking_id ? parseInt(booking_id) : undefined,
        package_id: package_id ? parseInt(package_id) : undefined,
        package_name: package_name,
        sub_package_id: sub_package_id ? parseInt(sub_package_id) : undefined,
        sub_package_name: sub_package_name,
        booking_date: booking_date,
        event_date: event_date,
        event_time: event_time,
        bill_code: bill_code, // If bill already created in Android app
        bill_url: bill_url
      });

      res.status(201).json({
        success: true,
        message: 'Payment bill created successfully',
        data: {
          billCode: result.billCode,
          billUrl: result.billUrl,
          amount: result.amount,
          status: result.status
        }
      });
    } catch (error) {
      next(error);
    }
  }

  /**
   * Get payment status by bill code
   * GET /api/payment/status/:billCode
   */
  async getPaymentStatus(req, res, next) {
    try {
      const { billCode } = req.params;

      if (!billCode) {
        throw new ValidationError('Bill code is required');
      }

      const result = await paymentService.getPaymentStatus(billCode);

      if (!result) {
        throw new NotFoundError('Payment not found');
      }

      res.status(200).json({
        success: true,
        data: result
      });
    } catch (error) {
      next(error);
    }
  }

  /**
   * Get all payments (optional, for admin)
   * GET /api/payment/list
   */
  async getAllPayments(req, res, next) {
    try {
      const { status, limit } = req.query;

      const payments = await paymentService.getAllPayments({
        status,
        limit: limit ? parseInt(limit) : undefined
      });

      res.status(200).json({
        success: true,
        count: payments.length,
        data: payments
      });
    } catch (error) {
      next(error);
    }
  }
}

module.exports = new PaymentController();

