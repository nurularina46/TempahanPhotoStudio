const toyyibpayService = require('./toyyibpay.service');
const supabaseService = require('./supabase.service');

class PaymentService {
  /**
   * Create a new payment
   * @param {Object} paymentData
   * @returns {Promise<Object>}
   */
  async createPayment(paymentData) {
    const { name, email, phone, amount, description, bill_code, bill_url } = paymentData;

    let toyyibpayResult = null;
    let billCode = null;
    let billUrl = null;

    // Step 1: Create bill in ToyyibPay (only if not already created in Android app)
    if (bill_code && bill_url) {
      // Bill already created in Android app, use provided bill_code and bill_url
      billCode = bill_code;
      billUrl = bill_url;
      console.log('Using existing bill from Android app:', billCode);
    } else {
      // Create new bill in ToyyibPay
      toyyibpayResult = await toyyibpayService.createBill({
        name,
        email,
        phone,
        amount,
        description
      });

      if (!toyyibpayResult.success) {
        throw new Error('Failed to create bill in ToyyibPay');
      }

      billCode = toyyibpayResult.billCode;
      billUrl = toyyibpayResult.billUrl;
    }

    // Step 2: Save to Supabase with full booking data
    const paymentRecord = await supabaseService.insertPayment({
      // User information
      user_name: name,
      user_email: email,
      user_phone: phone,
      
      // Payment information
      bill_code: billCode,
      bill_url: billUrl,
      amount: amount,
      payment_method: 'ToyyibPay',
      
      // Status
      status: 'Unpaid',
      payment_status: 'Unpaid',
      
      // Additional booking data (if provided)
      booking_id: paymentData.booking_id,
      user_id: paymentData.user_id,
      package_id: paymentData.package_id,
      package_name: paymentData.package_name,
      sub_package_id: paymentData.sub_package_id,
      sub_package_name: paymentData.sub_package_name,
      booking_date: paymentData.booking_date,
      event_date: paymentData.event_date,
      event_time: paymentData.event_time,
      notes: paymentData.notes || `Payment created via ${bill_code ? 'Android app' : 'backend'} - ${description}`
    });

    return {
      billCode: billCode,
      billUrl: billUrl,
      amount: amount,
      status: 'PENDING',
      paymentRecord
    };
  }

  /**
   * Get payment status
   * @param {String} billCode
   * @returns {Promise<Object>}
   */
  async getPaymentStatus(billCode) {
    // Step 1: Get from database
    const payment = await supabaseService.getPaymentByBillCode(billCode);

    if (!payment) {
      return null;
    }

    // Step 2: Get latest status from ToyyibPay
    try {
      const toyyibpayResult = await toyyibpayService.getBillTransactions(billCode);

      if (toyyibpayResult.success && toyyibpayResult.transactions.length > 0) {
        // Get the latest transaction
        const latestTransaction = toyyibpayResult.transactions[0];
        const toyyibpayStatus = toyyibpayService.mapStatus(latestTransaction.billpaymentStatus);

        // Update database if status changed
        if (payment.status !== toyyibpayStatus) {
          await supabaseService.updatePaymentStatus(billCode, toyyibpayStatus);
          payment.status = toyyibpayStatus;
        }

        return {
          ...payment,
          transactionDetails: latestTransaction,
          lastChecked: new Date().toISOString()
        };
      }
    } catch (error) {
      console.error('Error fetching from ToyyibPay:', error.message);
      // Return database status if ToyyibPay fails
    }

    return payment;
  }

  /**
   * Update payment from callback
   * @param {String} billCode
   * @param {String} toyyibpayStatus
   * @param {Object} callbackData - Additional callback data (transaction_id, order_id, etc)
   * @returns {Promise<Object>}
   */
  async updatePaymentFromCallback(billCode, toyyibpayStatus, callbackData = {}) {
    // Map ToyyibPay status to our status
    const status = toyyibpayService.mapStatus(toyyibpayStatus);
    
    // Prepare update data
    const updateData = {
      status: status,
      payment_status: status,
      toyyibpay_status: toyyibpayStatus,
      callback_received_at: new Date().toISOString()
    };

    // Add optional callback data
    if (callbackData.transaction_id) {
      updateData.toyyibpay_transaction_id = callbackData.transaction_id;
    }
    if (callbackData.order_id) {
      updateData.toyyibpay_order_id = callbackData.order_id;
    }

    // Update payment in database
    await supabaseService.updatePaymentStatus(billCode, updateData);

    console.log(`Payment ${billCode} updated to status: ${status}`);

    // Update associated order status if exists
    try {
      const payment = await supabaseService.getPaymentByBillCode(billCode);
      
      if (payment && payment.order_id) {
        // Get order to find order_number
        const order = await supabaseService.getOrderById(payment.order_id);
        
        if (order) {
          // Update order payment status
          const orderUpdateData = {
            payment_status: status
          };

          // If payment is successful, confirm the order
          if (status === 'Paid') {
            orderUpdateData.status = 'Confirmed';
            orderUpdateData.confirmed_at = new Date().toISOString();
          } else if (status === 'Failed') {
            orderUpdateData.status = 'PaymentFailed';
          }

          await supabaseService.updateOrderStatus(order.order_number, orderUpdateData);
          console.log(`Order ${order.order_number} updated with payment status: ${status}`);
        }
      }
    } catch (error) {
      console.error('Failed to update order status from payment callback:', error.message);
      // Don't throw error, payment update is still successful
    }

    return {
      billCode,
      status,
      ...updateData
    };
  }

  /**
   * Get all payments
   * @param {Object} filters
   * @returns {Promise<Array>}
   */
  async getAllPayments(filters = {}) {
    return await supabaseService.getAllPayments(filters);
  }
}

module.exports = new PaymentService();

