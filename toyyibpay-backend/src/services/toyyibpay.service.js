const axios = require('axios');
const querystring = require('querystring');
const toyyibpayConfig = require('../config/toyyibpay.config');

class ToyyibPayService {
  /**
   * Create a bill in ToyyibPay Sandbox
   * @param {Object} billData - Bill data
   * @returns {Promise<Object>}
   */
  async createBill(billData) {
    const { name, email, phone, amount, description } = billData;

    // Convert amount to cents (ToyyibPay requires amount in cents)
    const amountInCents = Math.round(parseFloat(amount) * 100);

    const params = {
      userSecretKey: toyyibpayConfig.secretKey,
      categoryCode: toyyibpayConfig.categoryCode,
      billName: description || `Payment for ${name}`,
      billDescription: description || `Payment transaction for ${name}`,
      billPriceSetting: 1, // 1 = Fixed price
      billPayorInfo: 1, // 1 = Require customer info
      billAmount: amountInCents,
      billReturnUrl: toyyibpayConfig.returnUrl,
      billCallbackUrl: toyyibpayConfig.callbackUrl,
      billExternalReferenceNo: `REF-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
      billTo: name,
      billEmail: email,
      billPhone: phone,
      billSplitPayment: 0, // 0 = No split payment
      billSplitPaymentArgs: '',
      billPaymentChannel: '0', // 0 = All channels
      billContentEmail: '',
      billChargeToCustomer: 0 // 0 = No additional charge
    };

    try {
      const response = await axios.post(
        toyyibpayConfig.getUrl('createBill'),
        querystring.stringify(params),
        {
          headers: toyyibpayConfig.getHeaders()
        }
      );

      const result = response.data;

      if (result[0]?.Status === '1') {
        return {
          success: true,
          billCode: result[0].BillCode,
          billUrl: `https://dev.toyyibpay.com/${result[0].BillCode}`,
          data: result[0]
        };
      } else {
        throw new Error(result[0]?.msg || 'Failed to create bill');
      }
    } catch (error) {
      console.error('ToyyibPay createBill error:', error.response?.data || error.message);
      throw new Error(error.response?.data?.msg || error.message || 'Failed to create bill');
    }
  }

  /**
   * Get bill transactions from ToyyibPay
   * @param {String} billCode - Bill code
   * @returns {Promise<Object>}
   */
  async getBillTransactions(billCode) {
    const params = {
      userSecretKey: toyyibpayConfig.secretKey,
      billCode: billCode
    };

    try {
      const response = await axios.post(
        toyyibpayConfig.getUrl('getBillTransactions'),
        querystring.stringify(params),
        {
          headers: toyyibpayConfig.getHeaders()
        }
      );

      const result = response.data;

      if (result[0]?.Status === '1') {
        return {
          success: true,
          transactions: result[0].transactions || [],
          data: result[0]
        };
      } else {
        return {
          success: false,
          message: result[0]?.msg || 'No transactions found',
          transactions: []
        };
      }
    } catch (error) {
      console.error('ToyyibPay getBillTransactions error:', error.response?.data || error.message);
      throw new Error(error.response?.data?.msg || error.message || 'Failed to get bill transactions');
    }
  }

  /**
   * Map ToyyibPay status to our status
   * @param {String} toyyibpayStatus - Status from ToyyibPay
   * @returns {String}
   */
  mapStatus(toyyibpayStatus) {
    const statusMap = {
      '1': 'PAID',
      '2': 'FAILED',
      '3': 'PENDING',
      '': 'PENDING'
    };

    return statusMap[toyyibpayStatus] || 'PENDING';
  }
}

module.exports = new ToyyibPayService();

