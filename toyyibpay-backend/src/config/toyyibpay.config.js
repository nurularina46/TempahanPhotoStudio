class ToyyibPayConfig {
  constructor() {
    this.secretKey = process.env.TOYYIBPAY_SECRET_KEY;
    this.categoryCode = process.env.TOYYIBPAY_CATEGORY_CODE;
    this.baseUrl = process.env.TOYYIBPAY_BASE_URL || 'https://dev.toyyibpay.com/index.php/api/';
    this.returnUrl = process.env.RETURN_URL;
    this.callbackUrl = process.env.CALLBACK_URL;

    if (!this.secretKey || !this.categoryCode) {
      throw new Error('ToyyibPay configuration is missing. Please check your .env file.');
    }
  }

  getHeaders() {
    return {
      'Content-Type': 'application/x-www-form-urlencoded'
    };
  }

  getUrl(endpoint) {
    return `${this.baseUrl}${endpoint}`;
  }
}

module.exports = new ToyyibPayConfig();

