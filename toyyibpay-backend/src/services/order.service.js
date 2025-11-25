const supabaseService = require('./supabase.service');
const paymentService = require('./payment.service');

class OrderService {
  /**
   * Create a new order with payment
   * @param {Object} orderData - Order data
   * @returns {Promise<Object>}
   */
  async createOrder(orderData) {
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
      discount_amount = 0,
      tax_amount = 0,
      total_amount,

      // Order details
      order_type = 'booking',
      notes,
      special_requests,
      terms_accepted = false,

      // Payment info (optional - if bill already created)
      bill_code,
      bill_url,
      
      metadata
    } = orderData;

    // Calculate total if not provided
    const finalTotalAmount = total_amount || 
      (parseFloat(base_amount) + parseFloat(tax_amount) - parseFloat(discount_amount));

    // Generate order number
    const orderNumber = this.generateOrderNumber();

    // Step 1: Create order in database
    const order = await supabaseService.insertOrder({
      order_number: orderNumber,
      order_type,
      user_id,
      user_name,
      user_email,
      user_phone,
      user_address,
      package_id,
      package_name,
      sub_package_id,
      sub_package_name,
      event_date,
      event_time,
      event_location,
      event_type,
      base_amount,
      discount_amount,
      tax_amount,
      total_amount: finalTotalAmount,
      payment_status: 'Unpaid',
      status: 'Pending',
      notes,
      special_requests,
      terms_accepted,
      metadata
    });

    // Step 2: Create payment (with or without bill_code)
    let payment = null;
    try {
      payment = await paymentService.createPayment({
        name: user_name,
        email: user_email,
        phone: user_phone,
        amount: finalTotalAmount,
        description: `Payment for Order ${orderNumber} - ${package_name || 'Booking'}`,
        
        // Link to order
        order_id: order.id,
        booking_id: order.id, // Same as order_id for backward compatibility
        
        // Pass all order details
        user_id,
        package_id,
        package_name,
        sub_package_id,
        sub_package_name,
        booking_date: order.created_at,
        event_date,
        event_time,
        notes: notes || `Order: ${orderNumber}`,
        
        // Use existing bill if provided
        bill_code,
        bill_url
      });

      // Step 3: Update order with payment_id
      await supabaseService.updateOrderStatus(orderNumber, {
        payment_id: order.id, // Link order to payment
        payment_method: 'ToyyibPay'
      });

    } catch (error) {
      console.error('Failed to create payment for order:', error.message);
      // Order still created, but payment failed
      // Mark order as having payment error
      await supabaseService.updateOrderStatus(orderNumber, {
        status: 'PaymentError',
        notes: `${notes || ''}\n\nPayment Error: ${error.message}`
      });
      
      throw new Error(`Order created but payment failed: ${error.message}`);
    }

    return {
      order: order,
      payment: payment,
      orderNumber: orderNumber,
      billCode: payment?.billCode,
      billUrl: payment?.billUrl,
      totalAmount: finalTotalAmount
    };
  }

  /**
   * Get order by order number
   * @param {String} orderNumber - Order number
   * @returns {Promise<Object>}
   */
  async getOrder(orderNumber) {
    const order = await supabaseService.getOrderByOrderNumber(orderNumber);
    
    if (!order) {
      return null;
    }

    // Get associated payment if exists
    if (order.payment_id) {
      try {
        const payment = await supabaseService.getPaymentByBillCode(order.payment_id);
        order.payment = payment;
      } catch (error) {
        console.error('Failed to fetch payment for order:', error.message);
      }
    }

    return order;
  }

  /**
   * Update order status
   * @param {String} orderNumber - Order number
   * @param {String} newStatus - New status
   * @param {Object} additionalData - Additional data to update
   * @returns {Promise<Object>}
   */
  async updateOrderStatus(orderNumber, newStatus, additionalData = {}) {
    const updateData = {
      status: newStatus,
      ...additionalData
    };

    return await supabaseService.updateOrderStatus(orderNumber, updateData);
  }

  /**
   * Update order payment status (called from payment callback)
   * @param {String} orderNumber - Order number
   * @param {String} paymentStatus - Payment status
   * @returns {Promise<Object>}
   */
  async updateOrderPaymentStatus(orderNumber, paymentStatus) {
    const updateData = {
      payment_status: paymentStatus
    };

    // If payment is successful, confirm the order
    if (paymentStatus === 'Paid') {
      updateData.status = 'Confirmed';
      updateData.confirmed_at = new Date().toISOString();
    }

    return await supabaseService.updateOrderStatus(orderNumber, updateData);
  }

  /**
   * Get all orders with filters
   * @param {Object} filters - Filters
   * @returns {Promise<Array>}
   */
  async getAllOrders(filters = {}) {
    return await supabaseService.getAllOrders(filters);
  }

  /**
   * Get orders by user email
   * @param {String} userEmail - User email
   * @returns {Promise<Array>}
   */
  async getUserOrders(userEmail) {
    return await supabaseService.getOrdersByUserEmail(userEmail);
  }

  /**
   * Cancel order
   * @param {String} orderNumber - Order number
   * @param {String} reason - Cancellation reason
   * @returns {Promise<Object>}
   */
  async cancelOrder(orderNumber, reason = '') {
    const order = await supabaseService.getOrderByOrderNumber(orderNumber);
    
    if (!order) {
      throw new Error('Order not found');
    }

    // Only allow cancellation for certain statuses
    if (['Completed', 'Cancelled'].includes(order.status)) {
      throw new Error(`Cannot cancel order with status: ${order.status}`);
    }

    // If payment was made, might need refund process
    if (order.payment_status === 'Paid') {
      console.warn(`Order ${orderNumber} was paid. Refund process needed.`);
      // TODO: Implement refund logic
    }

    return await supabaseService.updateOrderStatus(orderNumber, {
      status: 'Cancelled',
      payment_status: order.payment_status === 'Paid' ? 'Refunded' : order.payment_status,
      cancelled_at: new Date().toISOString(),
      notes: `${order.notes || ''}\n\nCancelled: ${reason}`
    });
  }

  /**
   * Generate order number
   * Format: ORD-YYYYMMDD-RANDOM6
   * @returns {String}
   */
  generateOrderNumber() {
    const date = new Date();
    const dateStr = date.toISOString().slice(0, 10).replace(/-/g, '');
    const random = Math.random().toString(36).substr(2, 6).toUpperCase();
    return `ORD-${dateStr}-${random}`;
  }
}

module.exports = new OrderService();

