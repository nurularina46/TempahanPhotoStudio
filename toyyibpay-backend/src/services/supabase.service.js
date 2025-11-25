const supabase = require('../config/supabase.config');

class SupabaseService {
  /**
   * Insert a new payment record with full booking data
   * @param {Object} paymentData - Payment data with all booking information
   * @returns {Promise<Object>}
   */
  async insertPayment(paymentData) {
    // Extract all fields from paymentData
    const {
      booking_id,
      user_id,
      user_name,
      user_email,
      user_phone,
      package_id,
      package_name,
      sub_package_id,
      sub_package_name,
      booking_date,
      event_date,
      event_time,
      bill_code,
      bill_url,
      amount,
      payment_method = 'ToyyibPay',
      status = 'Unpaid',
      payment_status = 'Unpaid',
      notes,
      toyyibpay_status,
      toyyibpay_transaction_id,
      toyyibpay_order_id
    } = paymentData;

    // Build data object with only provided fields
    const data = {
      bill_code, // Required
      user_id: user_id || null,
      user_name: user_name || '',
      user_email: user_email || '',
      user_phone: user_phone || null,
      amount: parseFloat(amount) || 0,
      payment_method: payment_method || 'ToyyibPay',
      status: status || 'Unpaid',
      payment_status: payment_status || 'Unpaid'
    };

    // Add optional fields if provided
    if (booking_id !== undefined) data.booking_id = booking_id;
    if (package_id !== undefined) data.package_id = package_id;
    if (package_name !== undefined) data.package_name = package_name;
    if (sub_package_id !== undefined) data.sub_package_id = sub_package_id;
    if (sub_package_name !== undefined) data.sub_package_name = sub_package_name;
    if (booking_date !== undefined) data.booking_date = booking_date;
    if (event_date !== undefined) data.event_date = event_date;
    if (event_time !== undefined) data.event_time = event_time;
    if (bill_url !== undefined) data.bill_url = bill_url;
    if (notes !== undefined) data.notes = notes;
    if (toyyibpay_status !== undefined) data.toyyibpay_status = toyyibpay_status;
    if (toyyibpay_transaction_id !== undefined) data.toyyibpay_transaction_id = toyyibpay_transaction_id;
    if (toyyibpay_order_id !== undefined) data.toyyibpay_order_id = toyyibpay_order_id;

    // Use service key for insert (full access)
    const result = await supabase.request('POST', 'payments', data, true);

    if (!result.success) {
      console.error('Supabase insert error:', result.error);
      throw new Error(result.error?.message || 'Failed to insert payment');
    }

    return result.data[0] || result.data;
  }

  /**
   * Update payment status by bill code
   * @param {String} billCode - Bill code
   * @param {Object} updateData - Update data (status, payment_status, toyyibpay_status, etc)
   * @returns {Promise<Object>}
   */
  async updatePaymentStatus(billCode, updateData) {
    // If updateData is a string, treat it as status (backward compatibility)
    const data = typeof updateData === 'string' 
      ? { status: updateData, payment_status: updateData }
      : updateData;

    // Add callback timestamp if status is being updated to Paid
    if (data.status === 'Paid' || data.payment_status === 'Paid') {
      data.callback_received_at = new Date().toISOString();
    }

    // Use service key for update (full access)
    const result = await supabase.request(
      'PATCH',
      `payments?bill_code=eq.${billCode}`,
      data,
      true
    );

    if (!result.success) {
      console.error('Supabase update error:', result.error);
      throw new Error(result.error?.message || 'Failed to update payment status');
    }

    return result.data && result.data.length > 0 ? result.data[0] : null;
  }

  /**
   * Get payment by bill code
   * @param {String} billCode - Bill code
   * @returns {Promise<Object|null>}
   */
  async getPaymentByBillCode(billCode) {
    const result = await supabase.request(
      'GET',
      `payments?bill_code=eq.${billCode}&select=*`,
      null,
      false
    );

    if (!result.success) {
      throw new Error(result.error?.message || 'Failed to get payment');
    }

    return result.data && result.data.length > 0 ? result.data[0] : null;
  }

  /**
   * Get all payments (optional, for admin)
   * @param {Object} filters - Optional filters
   * @returns {Promise<Array>}
   */
  async getAllPayments(filters = {}) {
    let endpoint = 'payments?select=*&order=created_at.desc';
    
    if (filters.status) {
      endpoint += `&status=eq.${filters.status}`;
    }
    
    if (filters.limit) {
      endpoint += `&limit=${filters.limit}`;
    }

    const result = await supabase.request('GET', endpoint, null, false);

    if (!result.success) {
      throw new Error(result.error?.message || 'Failed to get payments');
    }

    return result.data || [];
  }

  // ==================== ORDERS METHODS ====================

  /**
   * Insert a new order record
   * @param {Object} orderData - Order data
   * @returns {Promise<Object>}
   */
  async insertOrder(orderData) {
    const {
      order_number,
      order_type = 'booking',
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
      discount_amount = 0,
      tax_amount = 0,
      total_amount,
      payment_id,
      payment_status = 'Unpaid',
      payment_method,
      status = 'Pending',
      notes,
      special_requests,
      terms_accepted = false,
      metadata
    } = orderData;

    const data = {
      order_number: order_number || `ORD-${Date.now()}-${Math.random().toString(36).substr(2, 6).toUpperCase()}`,
      order_type: order_type || 'booking',
      user_name,
      user_email,
      base_amount: parseFloat(base_amount) || 0,
      discount_amount: parseFloat(discount_amount) || 0,
      tax_amount: parseFloat(tax_amount) || 0,
      total_amount: parseFloat(total_amount) || parseFloat(base_amount) || 0,
      payment_status: payment_status || 'Unpaid',
      status: status || 'Pending',
      terms_accepted: terms_accepted || false
    };

    // Add optional fields
    if (user_id !== undefined) data.user_id = user_id;
    if (user_phone !== undefined) data.user_phone = user_phone;
    if (user_address !== undefined) data.user_address = user_address;
    if (package_id !== undefined) data.package_id = package_id;
    if (package_name !== undefined) data.package_name = package_name;
    if (sub_package_id !== undefined) data.sub_package_id = sub_package_id;
    if (sub_package_name !== undefined) data.sub_package_name = sub_package_name;
    if (event_date !== undefined) data.event_date = event_date;
    if (event_time !== undefined) data.event_time = event_time;
    if (event_location !== undefined) data.event_location = event_location;
    if (event_type !== undefined) data.event_type = event_type;
    if (payment_id !== undefined) data.payment_id = payment_id;
    if (payment_method !== undefined) data.payment_method = payment_method;
    if (notes !== undefined) data.notes = notes;
    if (special_requests !== undefined) data.special_requests = special_requests;
    if (metadata !== undefined) data.metadata = metadata;

    const result = await supabase.request('POST', 'orders', data, true);

    if (!result.success) {
      console.error('Supabase insert order error:', result.error);
      throw new Error(result.error?.message || 'Failed to insert order');
    }

    return result.data[0] || result.data;
  }

  /**
   * Get order by order number
   * @param {String} orderNumber - Order number
   * @returns {Promise<Object|null>}
   */
  async getOrderByOrderNumber(orderNumber) {
    const result = await supabase.request(
      'GET',
      `orders?order_number=eq.${orderNumber}&select=*`,
      null,
      false
    );

    if (!result.success) {
      throw new Error(result.error?.message || 'Failed to get order');
    }

    return result.data && result.data.length > 0 ? result.data[0] : null;
  }

  /**
   * Get order by ID
   * @param {String} orderId - Order ID (UUID)
   * @returns {Promise<Object|null>}
   */
  async getOrderById(orderId) {
    const result = await supabase.request(
      'GET',
      `orders?id=eq.${orderId}&select=*`,
      null,
      false
    );

    if (!result.success) {
      throw new Error(result.error?.message || 'Failed to get order');
    }

    return result.data && result.data.length > 0 ? result.data[0] : null;
  }

  /**
   * Update order status
   * @param {String} orderNumber - Order number
   * @param {Object} updateData - Update data
   * @returns {Promise<Object>}
   */
  async updateOrderStatus(orderNumber, updateData) {
    const data = typeof updateData === 'string' 
      ? { status: updateData }
      : updateData;

    // Add timestamp based on status
    if (data.status === 'Confirmed' && !data.confirmed_at) {
      data.confirmed_at = new Date().toISOString();
    } else if (data.status === 'Completed' && !data.completed_at) {
      data.completed_at = new Date().toISOString();
    } else if (data.status === 'Cancelled' && !data.cancelled_at) {
      data.cancelled_at = new Date().toISOString();
    }

    const result = await supabase.request(
      'PATCH',
      `orders?order_number=eq.${orderNumber}`,
      data,
      true
    );

    if (!result.success) {
      console.error('Supabase update order error:', result.error);
      throw new Error(result.error?.message || 'Failed to update order');
    }

    return result.data && result.data.length > 0 ? result.data[0] : null;
  }

  /**
   * Get all orders with optional filters
   * @param {Object} filters - Optional filters
   * @returns {Promise<Array>}
   */
  async getAllOrders(filters = {}) {
    let endpoint = 'orders?select=*&order=created_at.desc';
    
    if (filters.status) {
      endpoint += `&status=eq.${filters.status}`;
    }

    if (filters.payment_status) {
      endpoint += `&payment_status=eq.${filters.payment_status}`;
    }

    if (filters.user_email) {
      endpoint += `&user_email=eq.${filters.user_email}`;
    }
    
    if (filters.limit) {
      endpoint += `&limit=${filters.limit}`;
    }

    const result = await supabase.request('GET', endpoint, null, false);

    if (!result.success) {
      throw new Error(result.error?.message || 'Failed to get orders');
    }

    return result.data || [];
  }

  /**
   * Get orders by user email
   * @param {String} userEmail - User email
   * @returns {Promise<Array>}
   */
  async getOrdersByUserEmail(userEmail) {
    const result = await supabase.request(
      'GET',
      `orders?user_email=eq.${userEmail}&select=*&order=created_at.desc`,
      null,
      false
    );

    if (!result.success) {
      throw new Error(result.error?.message || 'Failed to get orders');
    }

    return result.data || [];
  }
}

module.exports = new SupabaseService();

