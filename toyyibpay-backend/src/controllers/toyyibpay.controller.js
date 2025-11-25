const paymentService = require('../services/payment.service');

class ToyyibPayController {
  /**
   * Handle ToyyibPay callback
   * POST /api/toyyibpay/callback
   */
  async handleCallback(req, res, next) {
    try {
      // ToyyibPay sends data via POST form data or JSON
      const { billcode, order_id, status } = req.body;

      console.log('ToyyibPay Callback received:', { billcode, order_id, status });

      if (!billcode) {
        console.error('Callback missing billcode');
        return res.status(400).json({
          success: false,
          message: 'Bill code is required'
        });
      }

      // Update payment status in database
      await paymentService.updatePaymentFromCallback(billcode, status);

      // Always return 200 OK to ToyyibPay (they will retry if we return error)
      res.status(200).json({
        success: true,
        message: 'Callback processed successfully'
      });
    } catch (error) {
      console.error('Callback processing error:', error);
      // Still return 200 to prevent ToyyibPay from retrying
      // Log the error for manual investigation
      res.status(200).json({
        success: false,
        message: 'Callback received but processing failed',
        error: error.message
      });
    }
  }
}

module.exports = new ToyyibPayController();

